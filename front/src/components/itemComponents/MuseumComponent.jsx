import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChevronLeft, faSave } from '@fortawesome/free-solid-svg-icons';
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../../services/BackendService';
import { alertActions, store } from '../../utils/Rdx';
import { connect } from 'react-redux';

const MuseumComponent = props => {

    const index = useParams();
    const [name, setName] = useState('');
    const [location, setLocation] = useState('');
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();

    const handleChangeName = (e) => {
        setName(e.target.value)
    }

    const handleChangeLocation = (e) => {
        setLocation(e.target.value)
    }

    const refreshMuseum = (index) => {
        BackendService.retrieveMuseum(index.id)
            .then(resp => {
                setName(resp.data.name)
                setLocation(resp.data.location)
            })
            .catch(() => { setHidden(true) })
    }

    useEffect(() => {
        if (index.id != -1) {
            refreshMuseum(index);
        }
    }, [])

    const onSubmit = () => {
        if (name == "") {
            store.dispatch(alertActions.error("Название не может быть пустым!"))
        } else if (location == "") {
            store.dispatch(alertActions.error("Расположение не может быть пустым!"))
        } else {
            if (index.id == -1) {
                BackendService.createMuseum({name: name, location: location})
                .then(() => {navigate('/museums')})
                .catch(() => {})
            } else {
                BackendService.updateMuseum({id: index.id, name: name, location: location})
                .then(() => {navigate('/museums')})
                .catch(() => {})
            }
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Музей</h3>
                <div className="btn-toolbar">
                    <div className="btn-group ms-auto">
                        <button className="btn btn-outline-secondary"
                            onClick={() => {navigate(-1)}}>
                            <FontAwesomeIcon icon={faChevronLeft} />{' '}Назад
                        </button>
                    </div>
                </div>
            </div>
            <div>
                <form>
                    <label className="ms-2 mb-1 fs-5">Название музея</label>
                    <input type="text" className="form-control" 
                        name="name" value={name} autoComplete="off"
                        onChange={handleChangeName} />
                    <label className="ms-2 mt-2 mb-1 fs-5">Расположение</label>
                    <input type="text" className="form-control" 
                        name="location" value={location} autoComplete="off"
                        onChange={handleChangeLocation} />
                    <button className="btn btn-secondary mt-3" type="button"
                        onClick={onSubmit}>
                        <FontAwesomeIcon icon={faSave} />{" "}Сохранить
                    </button>
                </form>
            </div>
        </div>
    )
}

export default connect()(MuseumComponent);