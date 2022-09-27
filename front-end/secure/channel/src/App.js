import React from 'react';
import {Layout} from 'antd';
import AppRouter from './AppRouter'
import './App.css';
import 'antd/dist/antd.min.css';
import { Amplify, Auth } from 'aws-amplify';
import { Authenticator, Button, Heading } from '@aws-amplify/ui-react';
import '@aws-amplify/ui-react/styles.css';

import awsExports from './aws-exports';
Amplify.configure(awsExports);
Auth.configure(awsExports);

const {Content} = Layout;

const App = (props, user, signOut) => {
    return (
        <Authenticator hideSignUp={true}>
            <Layout>
                <Content><AppRouter {...props}/></Content>
            </Layout>
        </Authenticator>
    );
}

export default App;
