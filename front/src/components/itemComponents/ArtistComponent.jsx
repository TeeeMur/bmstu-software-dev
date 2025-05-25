import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChevronLeft, faSave } from '@fortawesome/free-solid-svg-icons';
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../../services/BackendService';
import { alertActions, store } from '../../utils/Rdx';
import { connect } from 'react-redux';

const ArtistComponent = props => {

    const index = useParams();
    const [name, setName] = useState('');
    const [country, setCountry] = useState('');
    const [century, setCentury] = useState('');
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();

    const handleChangeName = (e) => {
        setName(e.target.value)
    }

    const handleChangeCountry = (e) => {
        setCountry(e.target.value)
    }

    const handleChangeCentury = (e) => {
        setCentury(e.target.value)
    }

    const refreshArtist = (index) => {
        BackendService.retrieveArtist(index.id)
            .then(resp => {
                setName(resp.data.name)
                setCountry(resp.data.country.name)
                setCentury(resp.data.century)
            })
            .catch(() => { setHidden(true) })
    }

    useEffect(() => {
        if (index.id != -1) {
            refreshArtist(index);
        }
    }, [])

    const onSubmit = () => {
        if (name == "") {
            store.dispatch(alertActions.error("Название не может быть пустым!"))
        } else {
            console.log({id: index.id, name: name})
            if (index.id == -1) {
                BackendService.createArtist({name: name})
                .then(() => {navigate('/artists')})
                .catch(() => {})
            } else {
                BackendService.updateArtist({id: index.id, name: name})
                .then(() => {navigate('/artists')})
                .catch(() => {})
            }
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Художник</h3>
                <div className="btn-toolbar">
                    <div className="btn-group ms-auto">
                        <button className="btn btn-outline-secondary"
                            onClick={() => {navigate('/artists')}}>
                            <FontAwesomeIcon icon={faChevronLeft} />{' '}Назад
                        </button>
                    </div>
                </div>
            </div>
            <div>
                <form>
                    <label className="ms-2 mb-1 fs-5">Имя</label>
                    <input type="text" className="form-control" 
                        name={name} value={name} autoComplete="off"
                        onChange={handleChangeName} />
                    <label className="ms-2 mb-1 fs-5">Страна</label>
                    <input type="text" className="form-control" 
                        name={country} value={country} autoComplete="off"
                        onChange={handleChangeName} disabled="true"/>
                    <label className="ms-2 mb-1 fs-5">Век</label>
                    <input type="text" className="form-control" 
                        name={century} value={century} autoComplete="off"
                        onChange={handleChangeName} disabled="true"/>
                    <button className="btn btn-secondary mt-3" type="button"
                        onClick={onSubmit}>
                        <FontAwesomeIcon icon={faSave} />{" "}Сохранить
                    </button>
                </form>
            </div>
        </div>
    )
}

export default connect()(ArtistComponent);