package uk.gov.hmcts.reform.hmc.hmi.befta;

import uk.gov.hmcts.befta.BeftaTestDataLoader;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.ReflectionUtils;

import java.util.concurrent.ExecutionException;

public class InboundAdapterTestAutomationAdapter extends DefaultTestAutomationAdapter {


    @Override
    public String getNewS2SToken() {
        return null;
    }

    @Override
    public String getNewS2SToken(String clientId) {
        return null;
    }

    @Override
    public String getNewS2SToken(String clientId, String clientKey) {
        return null;
    }

    @Override
    public String getNewS2STokenWithEnvVars(String envVarNameForId, String envVarNameForKey) {
        return null;
    }

    @Override
    public void authenticate(UserData user, String preferredTokenClientId) throws ExecutionException {

    }

    @Override
    public synchronized Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key) {
        if (key.toString().startsWith("no_dynamic_injection_")) {
            return key.toString().replace("no_dynamic_injection_","");
        } else if (key.toString().startsWith("approximately ")) {
            try {
                String actualSizeFromHeaderStr = (String) ReflectionUtils.deepGetFieldInObject(scenarioContext,
                    "testData.actualResponse.headers.Content-Length");
                String expectedSizeStr = key.toString().replace("approximately ", "");

                int actualSize =  Integer.parseInt(actualSizeFromHeaderStr);
                int expectedSize = Integer.parseInt(expectedSizeStr);

                if (Math.abs(actualSize - expectedSize) < (actualSize * 10 / 100)) {
                    return actualSizeFromHeaderStr;
                }
                return expectedSize;
            } catch (Exception e) {
                throw new FunctionalTestException("Problem checking acceptable response payload: ", e);
            }
        } else if (key.toString().startsWith("contains ")) {
            try {
                String actualValueStr = (String) ReflectionUtils.deepGetFieldInObject(scenarioContext,
                    "testData.actualResponse.body.__plainTextValue__");
                String expectedValueStr = key.toString().replace("contains ", "");

                if (actualValueStr.contains(expectedValueStr)) {
                    return actualValueStr;
                }
                return "expectedValueStr " + expectedValueStr + " not present in response ";
            } catch (Exception e) {
                throw new FunctionalTestException("Problem checking acceptable response payload: ", e);
            }
        }
        return super.calculateCustomValue(scenarioContext, key);
    }

    @Override
    public BeftaTestDataLoader getDataLoader() {
        return new BeftaTestDataLoader() {
            @Override
            public void loadDataIfNotLoadedVeryRecently() {

            }

            @Override
            public boolean isTestDataLoadedForCurrentRound() {
                return false;
            }
        };
    }
}
