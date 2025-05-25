import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChevronLeft, faSave } from '@fortawesome/free-solid-svg-icons';
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../../services/BackendService';
import { alertActions, store } from '../../utils/Rdx';
import { connect } from 'react-redux';

const UserComponent = props => {

    const index = useParams();
    const [login, setLogin] = useState('');
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setLogin(e.target.value)
    }

    const refreshUser = (index) => {
        BackendService.retrieveUser(index.id)
            .then(resp => {
                setLogin(resp.data.login)
            })
            .catch(() => { setHidden(true) })
    }

    useEffect(() => {
        if (index.id != -1) {
            refreshUser(index);
        }
    }, [])

    const onSubmit = () => {
        if (login == "") {
            store.dispatch(alertActions.error("Название не может быть пустым!"))
        } else {
            console.log({id: index.id, login: login})
            if (index.id == -1) {
                BackendService.createUser({login: login})
                .then(() => {navigate('/users')})
                .catch(() => {})
            } else {
                BackendService.updateUser({id: index.id, login: login})
                .then(() => {navigate('/users')})
                .catch(() => {})
            }
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Пользователь</h3>
                <div className="btn-toolbar">
                    <div className="btn-group ms-auto">
                        <button className="btn btn-outline-secondary"
                            onClick={() => {navigate('/users')}}>
                            <FontAwesomeIcon icon={faChevronLeft} />{' '}Назад
                        </button>
                    </div>
                </div>
            </div>
            <div>
                <form>
                    <label className="ms-2 mb-1 fs-5">Логин</label>
                    <input type="text" className="form-control" 
                        login={login} value={login} autoComplete="off"
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

export default connect()(UserComponent);