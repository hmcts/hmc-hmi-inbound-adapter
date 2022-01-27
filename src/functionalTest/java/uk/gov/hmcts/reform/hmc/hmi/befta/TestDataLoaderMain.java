package uk.gov.hmcts.reform.hmc.hmi.befta;

public class TestDataLoaderMain {

    private TestDataLoaderMain() {
        // Hide Utility Class Constructor :
        // Utility classes should not have a public or default constructor (squid:S1118)
    }

    public static void main(String[] args) {
        new InboundAdapterTestAutomationAdapter().getDataLoader().loadDataIfNotLoadedVeryRecently();
    }

}
