import React from "react";
import AppHeader from "./Header";
import AuthService from "../services/AuthService";

function withHeader(WrappedComponent) {
    return class extends React.Component {
        constructor(props) {
            super(props);
        }

        render() {
            const isAuth = AuthService.isLoggedIn()

            return (
                <div>
                    { isAuth ? <AppHeader {...this.props}/> : null }
                    <WrappedComponent {...this.props}/>
                </div>
            )
        }
    }
}

export default withHeader