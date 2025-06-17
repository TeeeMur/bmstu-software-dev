import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChevronLeft, faSave } from '@fortawesome/free-solid-svg-icons';
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BackendService from '../../services/BackendService';
import { alertActions, store } from '../../utils/Rdx';
import { connect } from 'react-redux';

const PaintingComponent = props => {

    const index = useParams();
    const [name, setName] = useState('');
    const [artist, setArtist] = useState('');
    const [museum, setMuseum] = useState('');
    const [year, setYear] = useState('');
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();

    const handleChangeName = (e) => {
        setName(e.target.value)
    }

    const handleChangeArtist = (e) => {
        setArtist(e.target.value)
    }

    const handleChangeMuseum = (e) => {
        setMuseum(e.target.value)
    }

    const handleChangeYear = (e) => {
        setYear(e.target.value)
    }

    const refreshPainting = (index) => {
        BackendService.retrievePainting(index.id)
            .then(resp => {
                console.log(resp.data)
                setName(resp.data.name)
                setArtist(resp.data.artist.name)
                setMuseum(resp.data.museum.name)
                setYear(resp.data.year)
            })
            .catch(() => { setHidden(true) })
    }

    useEffect(() => {
        if (index.id != -1) {
            refreshPainting(index);
        }
    }, [])

    const onSubmit = () => {
        if (name === "") {
            store.dispatch(alertActions.error("Название не может быть пустым!"))
        } else if (year.match(/^[0-9]+$/) == null) {
            store.dispatch(alertActions.error("Год должен быть числом!"))
        }  else if (artist === "") {
            store.dispatch(alertActions.error("Автор не может быть пустым!"))
        } else if (museum === "") {
            store.dispatch(alertActions.error("Музей не может быть пустым!"))
        } else {
            if (index.id == -1) {
                BackendService.createPainting([{name: name, year: year, artist: {name: artist}, museum: {name: museum}}])
                .then((resp) => {navigate('/paintings')})
                .catch((resp) => {store.dispatch(alertActions.error(resp.response.data.data))})
            } else {
                BackendService.updatePainting({id: index.id, name: name})
                .then(() => {navigate('/paintings')})
                .catch((resp) => {store.dispatch(alertActions.error(resp.response.data.data))})
            }
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Картина</h3>
                <div className="btn-toolbar">
                    <div className="btn-group ms-auto">
                        <button className="btn btn-outline-secondary"
                            onClick={() => {navigate('/paintings')}}>
                            <FontAwesomeIcon icon={faChevronLeft} />{' '}Назад
                        </button>
                    </div>
                </div>
            </div>
            <div>
                <form>
                    <label className="ms-2 mb-1 fs-5">Название картины</label>
                    <input type="text" className="form-control" 
                        name={name} value={name} autoComplete="off"
                        onChange={handleChangeName} />
                    {index.id == -1 && 
                    <><label className="ms-2 mb-1 fs-5">Автор</label><input type="text" className="form-control"
                            name={artist} value={artist} autoComplete="off"
                            onChange={handleChangeArtist} /><label className="ms-2 mb-1 fs-5">Музей</label><input type="text" className="form-control"
                                name={museum} value={museum} autoComplete="off"
                                onChange={handleChangeMuseum} /><label className="ms-2 mb-1 fs-5">Год</label><input type="text" className="form-control"
                                    name={year} value={year} autoComplete="off"
                                    onChange={handleChangeYear} /></>
                    }
                    {
                        index.id != -1 &&
                        <><label className="ms-2 mb-1 fs-5">Автор</label><input type="text" className="form-control" disabled={true}
                            name={artist} value={artist} autoComplete="off"
                            onChange={handleChangeArtist} /><label className="ms-2 mb-1 fs-5">Музей</label><input type="text" className="form-control" disabled={true}
                                name={museum} value={museum} autoComplete="off"
                                onChange={handleChangeMuseum} /><label className="ms-2 mb-1 fs-5">Год</label><input type="text" className="form-control"
                                    name={year} value={year} autoComplete="off" disabled={true}
                                    onChange={handleChangeYear} /></>
                    }
                    <button className="btn btn-secondary mt-3" type="button"
                        onClick={onSubmit}>
                        <FontAwesomeIcon icon={faSave} />{" "}Сохранить
                    </button>
                </form>
            </div>
        </div>
    )
}

export default connect()(PaintingComponent);