import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChevronLeft, faSave } from '@fortawesome/free-solid-svg-icons';
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../services/BackendService';
import { alertActions, store } from '../utils/Rdx';
import { connect } from 'react-redux';

const CountryComponent = props => {

    const index = useParams();
    const [name, setName] = useState('');
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setName(e.target.value)
    }

    const refreshCountry = (index) => {
        BackendService.retrieveCountry(index.id)
            .then(resp => {
                setName(resp.data.name)
            })
            .catch(() => { setHidden(true) })
    }

    useEffect(() => {
        if (index.id != -1) {
            refreshCountry(index);
        }
    }, [])

    const onSubmit = () => {
        if (name == "") {
            store.dispatch(alertActions.error("Название не может быть пустым!"))
        } else {
            console.log({id: index.id, name: name})
            if (index.id == -1) {
                BackendService.createCountry({name: name})
                .then(() => {navigate('/countries')})
                .catch(() => {})
            } else {
                BackendService.updateCountry(index.id, {name: name})
                .then(() => {navigate('/countries')})
                .catch(() => {})
            }
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Страна</h3>
                <div className="btn-toolbar">
                    <div className="btn-group ms-auto">
                        <button className="btn btn-outline-secondary"
                            onClick={() => {navigate('/countries')}}>
                            <FontAwesomeIcon icon={faChevronLeft} />{' '}Назад
                        </button>
                    </div>
                </div>
            </div>
            <div>
                <form>
                    <label className="ms-2 mb-1 fs-5">Название страны</label>
                    <input type="text" className="form-control" 
                        name={name} value={name} autoComplete="off"
                        onChange={handleChange} />
                    <button className="btn btn-secondary mt-3" type="button"
                        onClick={onSubmit}>
                        <FontAwesomeIcon icon={faSave} />{" "}Сохранить
                    </button>
                </form>
            </div>
        </div>
    )
}

export default connect()(CountryComponent);