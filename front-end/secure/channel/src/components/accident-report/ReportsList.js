import {MessageOutlined} from '@ant-design/icons';
import {Col, List, message, Row, Space} from 'antd';
import React from 'react';
import withHeader from "../withHeader";
import AuthService from "../../services/AuthService";
import {MapContainer, Marker, TileLayer} from "react-leaflet";
import BaseAPI from "../../services/BaseAPI";
import {extend} from "leaflet/dist/leaflet-src.esm";
import moment from "moment";

const report = {
    id: "2334-ds231e-3fds34-s3sdf3442",
    policyHolderAccount: "Policy holder",
    registrationNumber: "A1233OP",
    collisionTime: "2022-08-15T12:23:44.000Z",
    collisionLocation: {
        latitude: 42.314068,
        longitude: 23.518080
    }
}
const reports = [report, report, report, report]

class DefList extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            reports: []
        }
    }

    componentDidMount() {
        BaseAPI.listReports().then(res => {
                this.setState({
                    reports: [...res.data.accidentReports]
                })
            }
        )
    }

    deleteReport(id) {
        BaseAPI.deleteReport(id).then(_ => {
            message.info("Report deleted successfully")
            this.props.history.push('/')
        })
    }

    render() {
        return (
            <div className="main-container">
                <Row justify="center">
                    <Col>
                        <List
                            itemLayout="vertical"
                            size="small"
                            pagination={{pageSize: 4, hideOnSinglePage: true}}
                            dataSource={this.state.reports}
                            renderItem={(item) => (
                                <List.Item
                                    key={item.id}
                                    extra={
                                        <div style={{width: '400px', height: '200px'}}>
                                            <MapContainer
                                                center={[item.collisionLocation.lat, item.collisionLocation.lng]}
                                                zoom={16}
                                                maxZoom={20}
                                                minZoom={10}
                                                style={{width: '100%', height: '100%'}}
                                            >
                                                <TileLayer
                                                    attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                                                    url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                                                />
                                                <Marker
                                                    key={item.collisionLocation.lat}
                                                    position={[item.collisionLocation.lat, item.collisionLocation.lng]}
                                                    interactive={false}
                                                />
                                            </MapContainer>
                                        </div>
                                    }
                                >
                                    <List.Item.Meta
                                        title={
                                            <span>
                                                <a onClick={() => this.props.history.push(`/reports/${item.id}`)}>
                                                    {item.id}
                                                </a>
                                                <a className="list-item-meta-extra"
                                                   onClick={() => this.deleteReport(item.id)}>
                                                    delete
                                                </a>
                                            </span>
                                        }
                                        description={AuthService.isPolicyHolder() ? null : `Report for: ${item.policyHolderAccount}`}
                                    />
                                    {<span>
                        An accident report for car with registration number <b>{item.registrationNumber}</b> and reported time <b>{moment(item.collisionTime).format("YYYY-MM-DD HH:mm:ss")}</b>
                    </span>
                                    }
                                </List.Item>
                            )}
                        />
                    </Col>
                </Row>
            </div>
        );
    }
}

const ReportsList = withHeader(DefList)
export default ReportsList;