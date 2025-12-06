import React, {useState, useEffect, useCallback} from 'react';
import {Routes, Route, useNavigate} from 'react-router-dom';
import SpecialOperationsPanel from './components/SpecialOperations/SpecialOperationsPanel';
import {useWebSocket} from "./hooks/useWebSocket.js";
import './App.css';
import {WS_URL} from "./utils/constants.js";
import PersonPage from "./components/Person/PersonPage.jsx";
import LocationPage from "./components/Location/LocationPage.jsx";
import CoordinatesPage from "./components/Coordinates/CoordinatesPage.jsx";
import ImportPage from "./components/Import/ImportPage.jsx";
import personService from "./services/mainService.js";

function App() {
    const [personRefreshTrigger, setPersonRefreshTrigger] = useState(0);
    const [personUpdateData, setPersonUpdateData] = useState(null);

    const [locationRefreshTrigger, setLocationRefreshTrigger] = useState(0);
    const [locationUpdateData, setLocationUpdateData] = useState(null);

    const [coordinatesRefreshTrigger, setCoordinatesRefreshTrigger] = useState(0);
    const [coordinatesUpdateData, setCoordinatesUpdateData] = useState(null);

    const [importRefreshTrigger, setImportRefreshTrigger] = useState(0);

    const [loading, setLoading] = useState(false);
    const [totalItems, setTotalItems] = useState([]);

    const navigate = useNavigate();


    const loadCounts = useCallback(async () => {
        setLoading(true);
        try {
            const response = await personService.getAllCounts();

            const itemsData = response.data || [];
            setTotalItems(itemsData);
        } catch (error) {
            console.error('Error loading counts:', error);
        } finally {
            setLoading(false);
        }
    }, []);

    // Загрузка при изменении параметров
    useEffect(() => {
        loadCounts();
    }, [loadCounts]);

    // Обработка WebSocket сообщений
    const handleWebSocketMessage = useCallback((message) => {
        console.log('🔄 WebSocket callback received:', message);

        const msg = JSON.parse(message);
        const action = msg.type;
        const entityIdStr = msg.id;
        const entity = msg.entity;
        const entityId = parseInt(entityIdStr);

        console.log('Action:', action, entity, 'ID:', entityId);
        loadCounts();

        switch (action) {
            case 'CREATED':
                switch (entity) {
                    case 'Person':
                        setPersonRefreshTrigger(prev => prev + 1);
                        break;
                    case 'Location':
                        setLocationRefreshTrigger(prev => prev + 1);
                        break;
                    case 'Coordinates':
                        setCoordinatesRefreshTrigger(prev => prev + 1);
                        break;
                    case 'Import':
                        setPersonRefreshTrigger(prev => prev + 1);
                        setLocationRefreshTrigger(prev => prev + 1);
                        setCoordinatesRefreshTrigger(prev => prev + 1);
                        setImportRefreshTrigger(prev => prev + 1);
                        break;
                }
                break;

            case 'UPDATED':
                switch (entity) {
                    case 'Person':
                        setPersonRefreshTrigger(prev => prev + 1);
                        setPersonUpdateData({entityId, action: 'UPDATED', data: {entityId, action}});
                        break;
                    case 'Location':
                        setLocationRefreshTrigger(prev => prev + 1);
                        setLocationUpdateData({entityId, action: 'UPDATED', data: {entityId, action}});
                        break;
                    case 'Coordinates':
                        setCoordinatesRefreshTrigger(prev => prev + 1);
                        setCoordinatesUpdateData({entityId, action: 'UPDATED', data: {entityId, action}});
                        break;
                }
                break;

            case 'DELETED':
                switch (entity) {
                    case 'Person':
                        setPersonUpdateData({entityId, action: 'DELETED'});
                        setPersonRefreshTrigger(prev => prev + 1);
                        break;
                    case 'Location':
                        setLocationUpdateData({entityId, action: 'DELETED'});
                        setLocationRefreshTrigger(prev => prev + 1);
                        break;
                    case 'Coordinates':
                        setCoordinatesUpdateData({entityId, action: 'DELETED'});
                        setCoordinatesRefreshTrigger(prev => prev + 1);
                        break;
                }
                break;

            case 'FAILURE_CREATED':
                switch (entity) {
                    case 'Import':
                        setImportRefreshTrigger(prev => prev + 1);
                        break;
                }
                break;

            default:
                console.log('Unknown action:', action);
        }
    }, [loadCounts]);

    // WebSocket
    const {isConnected} = useWebSocket(WS_URL, handleWebSocketMessage);

    return (
        <div className="app">
            <nav className="navbar">
                <div className="nav-container">
                        <h1 className="nav-title">Lab2</h1>
                        <div className="nav-select-container">
                            <select
                                className="nav-select"
                                onChange={(e) => {
                                    if (e.target.value) {
                                        navigate(e.target.value);
                                    }
                                }}
                                defaultValue=""
                            >
                                <option value="" disabled hidden>Navigate to...</option>
                                <option value="/">Dashboard</option>
                                <option value="/persons">All Persons</option>
                                <option value="/coordinates">All Coordinates</option>
                                <option value="/locations">All Locations</option>
                                <option value="/imports">History of imports</option>
                                <option value="/special-operations">Special Operations</option>
                            </select>
                        </div>
                    </div>
            </nav>

            <main className="main-content">
                <Routes>
                    <Route path="/" element={
                        <div className="dashboard">
                            <h1>Dashboard</h1>
                            <div className="stats-grid">
                                <div className="stat-card">
                                    <h3>WebSocket</h3>
                                    <p className={isConnected ? 'status-connected' : 'status-disconnected'}>
                                        {isConnected ? 'Connected' : 'Disconnected'}
                                    </p>
                                </div>
                            </div>
                            <div className="stats-grid">
                                <div className="stat-card">
                                    <h3>Total Persons</h3>
                                    <p>{totalItems.persons_count}</p>
                                </div>
                                <div className="stat-card">
                                    <h3>Total Coordinates</h3>
                                    <p>{totalItems.coordinates_count}</p>
                                </div>
                                <div className="stat-card">
                                    <h3>Total Locations</h3>
                                    <p>{totalItems.locations_count}</p>
                                </div>
                            </div>
                        </div>
                    }/>

                    <Route path="/persons" element={
                        <PersonPage
                            refreshTrigger={personRefreshTrigger}
                            personUpdateData={personUpdateData}
                            onPersonUpdateProcessed={() => setPersonUpdateData(null)}
                        />
                    }/>


                    <Route path="/coordinates" element={
                        <CoordinatesPage
                            refreshTrigger={coordinatesRefreshTrigger}
                            coordinatesUpdateData={coordinatesUpdateData}
                            onCoordinatesUpdateProcessed={() => setCoordinatesUpdateData(null)}
                        />
                    }/>

                    <Route path="/locations" element={
                        <LocationPage
                            refreshTrigger={locationRefreshTrigger}
                            locationUpdateData={locationUpdateData}
                            onLocationUpdateProcessed={() => setLocationUpdateData(null)}
                        />
                    }/>

                    <Route path="/imports" element={
                        <ImportPage
                            refreshTrigger={importRefreshTrigger}
                        />
                    }/>

                    <Route path="/special-operations" element={
                        <SpecialOperationsPanel/>
                    }/>
                </Routes>
            </main>
        </div>
    );
}

export default App;