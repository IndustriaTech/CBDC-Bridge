import AuthService from "./AuthService";

const axios = require('axios').default;

class BaseAPI {

    domainLegalFirm = 'http://localhost:20050';
    domainInsuranceCompany = 'http://localhost:10050';

    makeGetRequest(url, extraParams) {
        let domain;

        switch (AuthService.getRole()) {
            case "INSURANCE_COMPANY":
            case "POLICY_HOLDER":
                domain = this.domainInsuranceCompany;
                break;

            case "LEGAL_FIRM":
                domain = this.domainLegalFirm;
                break;

            default: domain = this.domainInsuranceCompany;
        }

        return axios
            .get(`${domain}/api/v1/${url}`, config(extraParams))
            .then(res => Promise.resolve(res))
            .catch(e => {
                throw new Error(e.message)
            });
    }

    makeDeleteRequest(url, extraHeaders) {
        let domain;

        switch (AuthService.getRole()) {
            case "INSURANCE_COMPANY":
            case "POLICY_HOLDER":
                domain = this.domainInsuranceCompany;
                break;

            case "LEGAL_FIRM":
                domain = this.domainLegalFirm;
                break;
        }

        return axios
            .delete(`${domain}/api/v1/${url}`, config(null, extraHeaders))
            .then(res => Promise.resolve(res))
            .catch(e => {
                throw new Error(e.message)
            });
    }

    submitForm(payload) {
        return axios
            .post(
                `${this.domainInsuranceCompany}/api/v1/accident-reports`,
                payload,
                config(null, {'x-role': AuthService.getRole()})
            )
            .then(res => Promise.resolve(res))
            .catch(e => {
                throw new Error(e.message)
            });
    }

    listReports = () => this.makeGetRequest(`accident-reports?role=${AuthService.getRole()}`);
    getReportById = (id) => this.makeGetRequest(`accident-reports/${id}?role=${AuthService.getRole()}`);
    deleteReport = (id) => this.makeDeleteRequest(`accident-reports/${id}?role=${AuthService.getRole()}`);
    getEvidenceById = (id) => this.makeGetRequest(`evidences/${id}?role=${AuthService.getRole()}`);
    listCollisionTypes = () => this.makeGetRequest('accident-reports/collisiontypes');
    listRoles = () => this.makeGetRequest('accident-reports/roles');
}

function config(extraParams, extraHeaders) {
    return {
        params: {...extraParams},
        headers: {...extraHeaders}
    }
}

export default new BaseAPI();
