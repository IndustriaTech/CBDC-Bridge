import React from "react";
import {Col, Row} from "antd";
import withHeader from "./withHeader";
import AuthService from "../services/AuthService";
import BaseAPI from "../services/BaseAPI";
import {UserOutlined} from "@ant-design/icons";

class DefLogin extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            roles: []
        }
    }

    componentDidMount() {
        BaseAPI.listRoles().then(res => {
            this.setState({
                    roles: [...res.data]
                })
            }
        )
    }

    render() {
        return (
            <div className="main-container">
                <Row align="middle" justify="space-evenly" style={{marginTop: '200px'}}>
                    {this.state.roles.map((role, index) =>
                        <Col
                            key={`role:${index}`}
                            span={6}
                            className="outer-box"
                            onClick={() => {
                                AuthService.login(role)
                                this.props.history.push('/dashboard')
                            }}
                        >
                            <div><UserOutlined style={{fontSize: '100px'}}/></div>
                            <span style={{fontSize: '25px'}}>{role}</span>
                        </Col>
                    )}
                </Row>
            </div>
        );
    }
}

const Login = withHeader(DefLogin)
export default Login
