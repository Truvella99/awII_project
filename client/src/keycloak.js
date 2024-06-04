import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: 'http://localhost:9090/auth',
    realm: 'CRMRealm',
    clientId: 'crmclient',
});

export default keycloak;
