locals {
  api_mgmt_name     = "cft-api-mgmt-${var.env}"
  api_mgmt_rg       = "cft-${var.env}-network-rg"
  hmc_key_vault     = "${var.product}-${var.env}"
  hmc_shared_rg     = "${var.product}-shared-${var.env}"
  api_base_path     = var.product
  gateway_client_id = "api_gw"
  idam_audience     = "hmi"

  hmi_inbound_adapter_url = "http://hmc-hmi-inbound-adapter-${var.env}.service.core-compute-${var.env}.internal"
  idam_url                = "${var.env == "prod" ? "https://hmcts-access.service.gov.uk" : "https://idam-web-public.${var.env}.platform.hmcts.net"}"
  oidc_issuer             = "https://forgerock-am.service.core-compute-idam-${var.env}.internal:8443/openam/oauth2/hmcts"
  s2s_url                 = "http://rpe-service-auth-provider-${var.env}.service.core-compute-${var.env}.internal"
}

provider "azurerm" {
  alias           = "aks-cftapps"
  subscription_id = var.aks_subscription_id
  features {}
}

data "azurerm_key_vault" "hmc_key_vault" {
  name                = local.hmc_key_vault
  resource_group_name = local.hmc_shared_rg
}

data "azurerm_key_vault_secret" "s2s_client_secret" {
  name         = "api-gateway-s2s-secret"
  key_vault_id = data.azurerm_key_vault.hmc_key_vault.id
}

module "api_mgmt_product" {
  source                = "git@github.com:hmcts/cnp-module-api-mgmt-product?ref=master"
  name                  = "${var.product}-${var.component}"
  subscription_required = "false"
  api_mgmt_name         = local.api_mgmt_name
  api_mgmt_rg           = local.api_mgmt_rg
  providers             = {
    azurerm = azurerm.aks-cftapps
  }
}

module "api_mgmt_api" {
  source        = "git@github.com:hmcts/cnp-module-api-mgmt-api?ref=master"
  name          = "${var.product}-api"
  display_name  = "HMC HMI Inbound Adapter API"
  api_mgmt_name = local.api_mgmt_name
  api_mgmt_rg   = local.api_mgmt_rg
  product_id    = module.api_mgmt_product.product_id
  path          = local.api_base_path
  service_url   = local.hmi_inbound_adapter_url
  protocols     = ["http", "https"]
  swagger_url   = "https://raw.githubusercontent.com/hmcts/reform-api-docs/master/docs/specs/hmc-hmi-inbound-adapter.json"
  revision      = "1"
  providers     = {
    azurerm = azurerm.aks-cftapps
  }
}

data "template_file" "policy_template" {
  template = file("${path.module}/template/api-policy.xml")

  vars = {
    idam_base_url     = local.idam_url
    oidc_issuer       = local.oidc_issuer
    audience          = local.idam_audience
    s2s_client_id     = local.gateway_client_id
    s2s_client_secret = data.azurerm_key_vault_secret.s2s_client_secret.value
    s2s_base_url      = local.s2s_url
  }
}

module "api_mgmt_policy" {
  source                 = "git@github.com:hmcts/cnp-module-api-mgmt-api-policy?ref=master"
  api_mgmt_name          = local.api_mgmt_name
  api_mgmt_rg            = local.api_mgmt_rg
  api_name               = module.api_mgmt_api.name
  api_policy_xml_content = data.template_file.policy_template.rendered
  providers              = {
    azurerm = azurerm.aks-cftapps
  }
}
