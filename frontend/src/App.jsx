import React, {useState, useEffect, useCallback, useRef} from 'react';
import {Routes, Route, Link} from 'react-router-dom';
import PersonTable from './components/Person/PersonTable';
import PersonForm from './components/Person/PersonForm';
import PersonView from './components/Person/PersonView';
import SpecialOperationsPanel from './components/SpecialOperations/SpecialOperationsPanel';
import Pagination from './components/common/Pagination';
import SearchBar from './components/common/SearchBar';
import LoadingSpinner from './components/common/LoadingSpinner';
import {useWebSocket} from "./hooks/useWebSocket.js";
import personService from './services/personService';
import './App.css';
import {WS_URL} from "./utils/constants.js";

function App() {
    const [persons, setPersons] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [searchTerm, setSearchTerm] = useState('');
    const [searchField, setSearchField] = useState('name');
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');

    const [showPersonForm, setShowPersonForm] = useState(false);
    const [showPersonView, setShowPersonView] = useState(false);
    const [selectedPerson, setSelectedPerson] = useState(null);
    const [editingPerson, setEditingPerson] = useState(null);

    // useEffect(() => {
    //     loadPersons();
    // }, [currentPage, pageSize, searchTerm, searchField, sortField, sortDirection]);
    //
    // const loadPersons = async () => {
    //     setLoading(true);
    //     try {
    //         const response = await personService.getAll(currentPage, pageSize, searchTerm, searchTerm === '' ? sortField : searchField, sortDirection);
    //         console.log(response.data);
    //         setPersons(response.data.persons || []);
    //         setTotalItems(response.data.totalCount || 0);
    //     } catch (error) {
    //         console.error('Error loading persons:', error);
    //         alert('Error loading persons: ' + error.message);
    //     } finally {
    //         setLoading(false);
    //     }
    // };
// Refs для хранения текущих данных без триггеринга ререндеров
    const currentPersonsRef = useRef([]);
    const currentParamsRef = useRef({});

    // WebSocket
    const {isConnected, lastMessage} = useWebSocket(WS_URL);

    // Загрузка данных
    const loadPersons = useCallback(async () => {
        setLoading(true);
        try {
            const response = await personService.getAll(
                currentPage,
                pageSize,
                searchTerm,
                searchTerm === '' ? sortField : searchField,
                sortDirection
            );

            const personsData = response.data.persons || [];
            setPersons(personsData);
            setTotalItems(response.data.totalCount || 0);

            // Сохраняем текущие данные в refs
            currentPersonsRef.current = personsData;
            currentParamsRef.current = {
                page: currentPage,
                size: pageSize,
                search: searchTerm,
                field: searchTerm === '' ? sortField : searchField,
                direction: sortDirection
            };

        } catch (error) {
            console.error('Error loading persons:', error);
            alert('Error loading persons: ' + error.message);
        } finally {
            setLoading(false);
        }
    }, [currentPage, pageSize, searchTerm, searchField, sortField, sortDirection]);

    // Загрузка при изменении параметров
    useEffect(() => {
        loadPersons();
    }, [loadPersons]);

    // Обработка WebSocket сообщений
    useEffect(() => {
        if (lastMessage) {
            handleWebSocketMessage(lastMessage);
        }
    }, [lastMessage]);

    const handleWebSocketMessage = (message) => {
        console.log('🔄 Processing WebSocket message:', message);

        const [action, personIdStr] = message.split(':');
        const personId = parseInt(personIdStr);

        switch (action) {
            case 'PERSON_CREATED':
                handlePersonCreated(personId);
                break;

            case 'PERSON_UPDATED':
                handlePersonUpdated(personId);
                break;

            case 'PERSON_DELETED':
                handlePersonDeleted(personId);
                break;

            default:
                console.log('Unknown action:', action);
        }
    };

    const handlePersonCreated = async (personId) => {
        console.log('Person created: id=', personId);

        // всегда перезагружаем - может надо добавить новую страницу внизу / новый Person станет подходить под фильтры
        await loadPersons();
    };

    const handlePersonUpdated = async (personId) => {
        console.log('Person updated: id=', personId);

        // нет смысла точечно обновлять, поскольку Person может перестать подходить по фильтрам
        await loadPersons();
        // Если мы редактируем эту персону, закрываем форму
        if (editingPerson && editingPerson.id === personId) {
            console.log('🚫 Closing edit form - person was updated by another user');
            setShowPersonForm(false);
            setEditingPerson(null);
            alert('This person was updated by another user. Please review changes.');
        }

        // Если мы просматриваем эту персону, обновляем данные
        if (selectedPerson && selectedPerson.id === personId) {
            console.log('🔄 Refreshing person view');
            try {
                const response = await personService.getById(personId);
                setSelectedPerson(response.data);
            } catch (error) {
                console.error('Error refreshing person view:', error);
            }
        }
    };

    const handlePersonDeleted = async (personId) => {
        console.log('Person deleted: id=', personId);

        // после удаления может понадобиться обновить количество элементов
        await loadPersons();
        // Закрываем формы если удалена редактируемая/просматриваемая персона
        if ((editingPerson && editingPerson.id === personId) ||
            (selectedPerson && selectedPerson.id === personId)) {
            console.log('🚫 Closing modals - person was deleted');
            setShowPersonForm(false);
            setShowPersonView(false);
            setEditingPerson(null);
            setSelectedPerson(null);
            alert('This person was deleted by another user.');
        }
    };

    const handleSearch = (term, field) => {
        setSearchTerm(term);
        setSearchField(field);
        console.log(term === '');
        if (term !== '') setCurrentPage(0);
    };

    const handleSort = (field) => {
        const newSortDirection = sortField === field
            ? (sortDirection === 'asc' ? 'desc' : 'asc')
            : 'asc';

        setSortField(field);
        setSortDirection(newSortDirection);
        setCurrentPage(0);
    };

    const handleCreatePerson = () => {
        setEditingPerson(null);
        setShowPersonForm(true);
    };

    const handleEditPerson = (person) => {
        setEditingPerson(person);
        setShowPersonForm(true);
    };

    const handleViewPerson = (person) => {
        setSelectedPerson(person);
        setShowPersonView(true);
    };

    const handleDeletePerson = async (person) => {
        if (window.confirm(`Are you sure you want to delete ${person.name}?`)) {
            try {
                await personService.delete(person.id);
                await loadPersons();
                alert('Person deleted successfully');
            } catch (error) {
                alert('Error deleting person: ' + error.message);
            }
        }
    };

    const handleSavePerson = async (savedPerson) => {
        setShowPersonForm(false);
        setEditingPerson(null);
        await loadPersons();
        alert(`Person ${savedPerson.id ? 'created' : 'updated'} successfully`);
    };

    const handleCancelForm = () => {
        setShowPersonForm(false);
        setEditingPerson(null);
    };

    return (
        <div className="app">
            <nav className="navbar">
                <div className="nav-container">
                    <h1 className="nav-title">Person Management System</h1>
                    <ul className="nav-menu">
                        <li><Link to="/">Dashboard</Link></li>
                        <li><Link to="/persons">All Persons</Link></li>
                        <li><Link to="/special-operations">Special Operations</Link></li>
                    </ul>
                </div>
            </nav>

            <main className="main-content">
                <Routes>
                    <Route path="/" element={
                        <div className="dashboard">
                            <h2>Dashboard</h2>
                            <div className="stats-grid">
                                <div className="stat-card">
                                    <h3>Total Persons</h3>
                                    <p>{totalItems}</p>
                                </div>
                                <div className="stat-card">
                                    <h3>Current Page</h3>
                                    <p>{currentPage + 1}</p>
                                </div>
                                <div className="stat-card">
                                    <h3>Page Size</h3>
                                    <p>{pageSize}</p>
                                </div>
                                <div className="stat-card">
                                    <h3>WebSocket</h3>
                                    <p className={isConnected ? 'status-connected' : 'status-disconnected'}>
                                        {isConnected ? 'Connected' : 'Disconnected'}
                                    </p>
                                </div>
                            </div>
                        </div>
                    }/>

                    <Route path="/persons" element={
                        <div className="persons-page">
                            <div className="page-header">
                                <h2>Person Management</h2>
                                <div className={`ws-badge ${isConnected ? 'connected' : 'disconnected'}`}>
                                    {isConnected ? '🟢 Live Updates' : '🔴 Offline'}
                                </div>
                                <button onClick={handleCreatePerson} className="btn-primary">
                                    Add New Person
                                </button>
                            </div>

                            <div className="controls">
                                <SearchBar
                                    onSearch={handleSearch}
                                    term={searchTerm}
                                    field={searchField}
                                />
                            </div>

                            {loading ? (
                                <LoadingSpinner text="Loading persons..."/>
                            ) : (
                                <>
                                    <PersonTable
                                        persons={persons}
                                        onEdit={handleEditPerson}
                                        onDelete={handleDeletePerson}
                                        onView={handleViewPerson}
                                        onSort={handleSort}
                                        sortField={sortField}
                                        sortDirection={sortDirection}
                                    />

                                    <Pagination
                                        currentPage={currentPage}
                                        totalPages={persons.length === pageSize ? Math.ceil(totalItems / pageSize) : Math.ceil(persons.length / pageSize)}
                                        onPageChange={setCurrentPage}
                                        pageSize={pageSize}
                                        onPageSizeChange={setPageSize}
                                        totalItems={totalItems}
                                    />
                                </>
                            )}
                        </div>
                    }/>

                    <Route path="/special-operations" element={
                        <SpecialOperationsPanel/>
                    }/>
                </Routes>

                {/* Modals */}
                {showPersonForm && (
                    <div className="modal-overlay">
                        <div className="modal">
                            <PersonForm
                                person={editingPerson}
                                onSave={handleSavePerson}
                                onCancel={handleCancelForm}
                                isEditing={!!editingPerson}
                            />
                        </div>
                    </div>
                )}

                {showPersonView && (
                    <PersonView
                        person={selectedPerson}
                        onClose={() => setShowPersonView(false)}
                        onEdit={handleEditPerson}
                    />
                )}
            </main>
        </div>
    );
}

export default App;