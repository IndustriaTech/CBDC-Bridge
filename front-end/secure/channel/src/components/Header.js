import React from 'react';
import {Button, PageHeader} from 'antd';
import AuthService from "../services/AuthService";

const AppHeader = (props) => {
    const name = sessionStorage.getItem('user')

    function getButtonRedirect() {
        return props.location.pathname !== '/submit-form' ?
            props.history.push('/submit-form') :
            props.history.push('/dashboard')
    }

    function getButtonType() {
        return props.location.pathname !== '/submit-form' ? "Submit form" : "Dashboard"
    }

    let submitButton = AuthService.isPolicyHolder() ?
        <Button
            key={'client-button'}
            danger
            onClick={() => {getButtonRedirect()}}
        >
            {getButtonType()}
        </Button> : null

    return (
        <div className="site-page-header">
            <PageHeader
                onBack={null}
                title={`Welcome ${name}`}
                extra={[
                    submitButton,
                    <Button
                        onClick={() => {
                            AuthService.logout()
                            props.history.push('/')
                        }}
                    >
                        Logout
                    </Button>
                ]}
            />
        </div>

    );
}

export default AppHeader;