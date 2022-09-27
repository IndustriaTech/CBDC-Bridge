class AuthService {
    constructor() {
        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);
    }

    login = (user) => this._setToken(user);
    logout = () => sessionStorage.removeItem('user')

    getRole = () => this._getToken()
    isLoggedIn = () => this._getToken() !== null
    isPolicyHolder = () => this._getToken() && this._getToken().toLowerCase().includes("holder")

    _setToken = (user) => sessionStorage.setItem('user', user);
    _getToken = () => sessionStorage.getItem('user');
}

export default new AuthService();
