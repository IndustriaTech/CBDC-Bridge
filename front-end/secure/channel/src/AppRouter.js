import React from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';
import SubmitForm from "./components/accident-report/SubmitForm";
import Login from "./components/Login";
import ReportsList from "./components/accident-report/ReportsList";
import AuthService from "./services/AuthService";
import {Redirect} from "react-router";
import SingleReport from "./components/accident-report/SingleReport";

const AppRouter = () => {
        return (
            <BrowserRouter>
                <Switch>
                    <Route exact path='/' render={(props) =>
                        AuthService.isLoggedIn() ? <Redirect to="/dashboard" {...props}/> : <Login  {...props}/>}
                    />;
                    <Route exact path='/submit-form' render={(props) =>
                        AuthService.isLoggedIn() ? <SubmitForm {...props}/> : <Redirect to="/" /> }
                    />;
                    <Route exact path='/dashboard' render={(props) =>
                        AuthService.isLoggedIn() ? <ReportsList {...props}/> : <Redirect to="/" /> }
                    />;
                    <Route exact path='/reports/:id' render={(props) =>
                        AuthService.isLoggedIn() ? <SingleReport {...props}/> : <Redirect to="/" /> }
                    />;
                </Switch>
            </BrowserRouter>
        )
}

export default AppRouter