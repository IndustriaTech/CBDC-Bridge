import React from 'react';
import {PlusOutlined, CloseOutlined, CheckOutlined} from '@ant-design/icons';
import {
    Form,
    Input,
    Button,
    Select,
    DatePicker,
    Upload,
    Switch, Row, Col, message
} from 'antd';
import 'leaflet/dist/leaflet.css';
import {MapContainer, TileLayer, useMapEvents} from "react-leaflet";
import L from "leaflet"
import withHeader from "../withHeader";
import BaseAPI from "../../services/BaseAPI";
import moment from "moment";

function MyComponent({saveMarkers}) {
    const map = useMapEvents({
        click: (e) => {
            const {lat, lng} = e.latlng;
            L.marker([lat, lng], {draggable:true}).addTo(map);
            saveMarkers(e.latlng);
        }
    });
    return null;
}

class DefSubmitForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            collisionLocation: {lat: 51.501067, lng: -0.127405},
            collisionTypes: [],
            evidences: []
        }
    }

    componentDidMount() {
        BaseAPI.listCollisionTypes().then(res =>
            this.setState({
                collisionTypes: [...res.data]
            })
        )
    }

    onFinish = (values) => {
        let mappedValues = {
            claimForm: {
                ...values,
                liabilityAdmitted: values['liabilityAdmitted'] ? values['liabilityAdmitted'] : false,
                medicalAssistanceNeeded: values['medicalAssistanceNeeded'] ? values['medicalAssistanceNeeded'] : false,
                vehicleReplacementNeeded: values['vehicleReplacementNeeded'] ? values['vehicleReplacementNeeded'] : false,
                collisionLocation: this.state.collisionLocation
            },
            evidences: this.state.evidences.map(e => ({
                name: e.name,
                size: e.size,
                bytes: e.thumbUrl.split(',')[1]
            }))
        }
        BaseAPI.submitForm(mappedValues).then(res => res.log).then(() => {
                message.success("Report created successfully").then(() =>
                    this.props.history.push('/dashboard')
                )
            }
        ).catch(e => message.error(e.message))
    };

    saveMarkers = (newMarkerCoords) => {
        this.setState({
            collisionLocation: newMarkerCoords
        });
    };

    onFileChange = (info) => {
        this.setState({
            evidences: [...info.fileList]
        })
    }

    beforeUpload() {
        console.log("Before upload");
    }

    render() {
        const props = {
            onChange: this.onFileChange,
            beforeUpload: this.beforeUpload,
            multiple: true,
        };

        return (
            <div className='main-container'>
                <Row justify="center" style={{marginTop: '50px'}}>
                    <Col>
                        <Form
                            labelCol={{span: 6}}
                            wrapperCol={{span: 18}}
                            layout="horizontal"
                            onFinish={this.onFinish}
                        >
                            <Form.Item label="Registration number" name='registrationNumber'>
                                <Select>
                                    <Select.Option value="BD51 SMR">BD51 SMR</Select.Option>
                                    <Select.Option value="AB47 XYZ">AB47 XYZ</Select.Option>
                                </Select>
                            </Form.Item>
                            <Form.Item label="Policy number" name='policyNumber'>
                                <Select>
                                    <Select.Option value="562786441">562786441</Select.Option>
                                    <Select.Option value="364521147">364521147</Select.Option>
                                </Select>
                            </Form.Item>
                            <Form.Item label="Collision time" name='collisionTime'>
                                <DatePicker disabledDate={(current) => current.isAfter(moment())} showTime/>
                            </Form.Item>
                            <Form.Item label="Type" name='collisionType'>
                                <Select
                                    mode="multiple"
                                    allowClear
                                    style={{
                                        width: '100%',
                                    }}
                                    placeholder="Please select"
                                >
                                    {this.state.collisionTypes.map(c =>
                                        <Select.Option key={c} value={c}>{c}</Select.Option>
                                    )}
                                </Select>
                            </Form.Item>
                            <Form.Item
                                label="Liability admitted"
                                name='liabilityAdmitted'
                                valuePropName="checked"
                            >
                                <Switch
                                    checkedChildren={<CheckOutlined/>}
                                    unCheckedChildren={<CloseOutlined/>}
                                />
                            </Form.Item>
                            <Form.Item
                                label="Medical assistance needed"
                                valuePropName="checked"
                                name='medicalAssistanceNeeded'
                            >
                                <Switch
                                    checkedChildren={<CheckOutlined/>}
                                    unCheckedChildren={<CloseOutlined/>}
                                />
                            </Form.Item>
                            <Form.Item
                                label="Replacement vehicle needed"
                                valuePropName="checked"
                                name='vehicleReplacementNeeded'
                            >
                                <Switch
                                    checkedChildren={<CheckOutlined/>}
                                    unCheckedChildren={<CloseOutlined/>}
                                />
                            </Form.Item>
                            <Form.Item label="Emergency contact details">
                                <Input.Group compact>
                                    <Form.Item
                                        name={['emergencyContactInfo', 'firstName']}
                                        noStyle
                                        rules={[{required: true, message: 'Emergency contract first name is required'}]}
                                    >
                                        <Input style={{width: '50%'}} placeholder="First name"/>
                                    </Form.Item>
                                    <Form.Item
                                        name={['emergencyContactInfo', 'lastName']}
                                        noStyle
                                        rules={[{required: true, message: 'Emergency contract last name is required'}]}
                                    >
                                        <Input style={{width: '50%'}} placeholder="Last name"/>
                                    </Form.Item>
                                    <Form.Item
                                        name={['emergencyContactInfo', 'phoneNumber']}
                                        noStyle
                                        rules={[{
                                            required: true,
                                            message: 'Emergency contract phone number is required'
                                        }]}
                                    >
                                        <Input placeholder="Phone number"/>
                                    </Form.Item>
                                </Input.Group>
                            </Form.Item>
                            <Form.Item label="Select accident location">
                                <MapContainer
                                    center={[this.state.collisionLocation.lat, this.state.collisionLocation.lng]}
                                    zoom={16}
                                    maxZoom={20}
                                    minZoom={10}
                                    style={{width: '100%', height: '400px'}}
                                >
                                    <TileLayer
                                        // attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                                        url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                                    />
                                    <MyComponent marker={this.state.collisionLocation} saveMarkers={this.saveMarkers}/>
                                </MapContainer>
                            </Form.Item>
                            <Form.Item label="Upload evidences">
                                <Upload {...props} listType="picture-card">
                                    <div>
                                        <PlusOutlined/>
                                        <div style={{marginTop: 8}}>Upload evidences</div>
                                    </div>
                                </Upload>
                            </Form.Item>
                            <Form.Item label="" colon={false}>
                                <Button type="primary" htmlType="submit">
                                    Submit
                                </Button>
                            </Form.Item>
                        </Form>
                    </Col>
                </Row>
            </div>
        );
    }
}

const SubmitForm = withHeader(DefSubmitForm)
export default SubmitForm;