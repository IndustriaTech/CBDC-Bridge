import {Card, Col, Descriptions, Modal, Row} from 'antd';
import React from 'react';
import withHeader from "../withHeader";
import BaseAPI from "../../services/BaseAPI";
import moment from "moment";
import {MapContainer, Marker, TileLayer} from "react-leaflet";
import {CheckOutlined, CloseOutlined, ArrowLeftOutlined} from "@ant-design/icons";
import {Document} from "react-pdf";

const {Meta} = Card;

class DefSingleReport extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            report: {
                policyHolderAccount: "",
                policyNumber: "",
                registrationNumber: "",
                collisionTime: "",
                collisionType: [],
                collisionLocation: {
                    lat: 0,
                    lng: 0
                },
                emergencyContactInfo: {
                    firstName: "",
                    lastName: "",
                    phoneNumber: ""
                },
                liabilityAdmitted: false,
                medicalAssistanceNeeded: false,
                vehicleReplacementNeeded: false,
                evidences: [],
                txDetails: ""
            },
            currentEvidence: "",
            isModalVisible: false
        }
    }

    componentDidMount() {
        let id = this.props.match.params.id
        BaseAPI.getReportById(id).then(res => {
                res.data.evidences.map(e =>
                    BaseAPI.getEvidenceById(e).then(evRes => {
                            this.setState(prevState => ({
                                report: {
                                    ...res.data,
                                    evidences: [
                                        ...prevState.report.evidences.filter(ev => ev.name !== evRes.data.name),
                                        evRes.data
                                    ]
                                },
                                currentEvidence: evRes.data.name
                            }))
                        }
                    )
                )
            }
        )
    }

    showModal = (id) => {
        this.setState({
            currentEvidence: id,
            isModalVisible: true
        })
    };

    handleCancel = () => {
        this.setState({
            isModalVisible: false
        })
    };

    render() {
        return (
            <div className="main-container">
                <div style={{width: '100%', height: '250px'}}>
                    {this.state.report.collisionLocation.lng !== 0 ? <MapContainer
                        center={[this.state.report.collisionLocation.lat, this.state.report.collisionLocation.lng]}
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
                            key={this.state.report.collisionLocation.lat}
                            position={[this.state.report.collisionLocation.lat, this.state.report.collisionLocation.lng]}
                            interactive={false}
                        />
                    </MapContainer> : null}
                </div>
                <Row justify="center">
                    <div style={{width: '40%', marginTop: '20px'}}>
                        {
                            <Descriptions
                                bordered
                                title={
                                    <span>
                                <ArrowLeftOutlined onClick={() => window.history.back()}/>
                                        {` Accident report: ${this.state.report.id}`}
                            </span>
                                }
                                labelStyle={{fontWeight: 'bold', background: 'white'}}
                                contentStyle={{background: 'white'}}
                                column={1}
                                size='small'
                            >
                                <Descriptions.Item label="Data input" style={{background: '#61dafb'}}/>
                                <Descriptions.Item label="Registration number">
                                    {this.state.report.registrationNumber}
                                </Descriptions.Item>
                                <Descriptions.Item label="Policy number">
                                    {this.state.report.policyNumber}
                                </Descriptions.Item>
                                <Descriptions.Item label="Collision date (DD/MM/YYYY)">
                                    {moment(this.state.report.collisionTime).format('DD/MM/YYYY')}
                                </Descriptions.Item>
                                <Descriptions.Item label="Collision time (Hour: Minute: Second)">
                                    {moment(this.state.report.collisionTime).format('HH:MM:SS')}
                                </Descriptions.Item>
                                <Descriptions.Item label="Collision type">
                                    {this.state.report.collisionType.join(', ').replaceAll('_', ' ')}
                                </Descriptions.Item>
                                <Descriptions.Item label="Liability admitted">
                                    {this.state.report.liabilityAdmitted ? <CheckOutlined/> : <CloseOutlined/>}
                                </Descriptions.Item>
                                <Descriptions.Item label="Medical assistance needed">
                                    {this.state.report.medicalAssistanceNeeded ? <CheckOutlined/> : <CloseOutlined/>}
                                </Descriptions.Item>
                                <Descriptions.Item label="Replacement vehicle needed">
                                    {this.state.report.vehicleReplacementNeeded ? <CheckOutlined/> : <CloseOutlined/>}
                                </Descriptions.Item>
                                <Descriptions.Item label="Emergency contact details">
                                    {this.state.report.emergencyContactInfo.firstName}&nbsp;
                                    {this.state.report.emergencyContactInfo.lastName}&nbsp;
                                    {this.state.report.emergencyContactInfo.phoneNumber}
                                </Descriptions.Item>
                                <Descriptions.Item label="Documents uploaded" style={{background: '#61dafb'}}/>
                                {this.state.report.evidences && this.state.report.evidences.sort(function (a, b) { return a.type - b.type }).map((e, index) =>
                                    e.type !== "NOTIFICATION_FORM" ?
                                        <Descriptions.Item label={`Picture ${index + 1}`}>
                                            <a onClick={() => this.showModal(e.name)}>{e.name}</a>
                                        </Descriptions.Item> :
                                        <Descriptions.Item label="Claim notification form">
                                            <a onClick={() => this.showModal(e.name)}>{e.name}</a>
                                        </Descriptions.Item>
                                )}
                            </Descriptions>
                        }
                    </div>
                </Row>
                <Modal
                    visible={this.state.isModalVisible}
                    bodyStyle={{height: '60vh', overflowY: 'auto'}}
                    width='60%'
                    onOk={this.handleCancel}
                    onCancel={this.handleCancel}
                >
                    {
                        this.state.report.evidences.length > 0 && (this.state.report.evidences.filter(e => e.name === this.state.currentEvidence)[0].type === "IMAGE" ?
                                <img
                                    alt="example"
                                    src={`data:image/jpeg;base64,${this.state.report.evidences.filter(e => e.name === this.state.currentEvidence)[0].bytes}`}
                                />
                                :
                                <embed
                                    width='100%'
                                    height='100%'
                                    src={`data:application/pdf;base64,${this.state.report.evidences.filter(e => e.name === this.state.currentEvidence)[0].bytes}`}/>
                        )
                    }
                </Modal>
            </div>
        );
    }
}

const SingleReport = withHeader(DefSingleReport)
export default SingleReport;