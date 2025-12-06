import PersonTable from "./PersonTable.jsx";
import React, {useCallback, useEffect, useState} from "react";
import personService from "../../services/mainService.js";
import SearchBar from "../common/SearchBar.jsx";
import LoadingSpinner from "../common/LoadingSpinner.jsx";
import Pagination from "../common/Pagination.jsx";
import PersonView from "./PersonView.jsx";
import PersonForm from "./PersonForm.jsx";

const PersonPage = ({ refreshTrigger, personUpdateData, onPersonUpdateProcessed }) => {
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
        } catch (error) {
            console.error('Error loading persons:', error);
        } finally {
            setLoading(false);
        }
    }, [currentPage, pageSize, searchTerm, searchField, sortField, sortDirection]);

    // Загрузка при изменении параметров
    useEffect(() => {
        loadPersons();
    }, [loadPersons]);

    // Реакция на триггер обновления
    useEffect(() => {
        if (refreshTrigger > 0) {
            loadPersons();
        }
    }, [refreshTrigger, loadPersons]);

    // Реакция на конкретные обновления Person
    useEffect(() => {
        if (personUpdateData) {
            const { personId, action } = personUpdateData;

            switch (action) {
                case 'UPDATED':
                    // Если мы редактируем эту персону, закрываем форму
                    if (editingPerson && editingPerson.id === personId) {
                        console.log('🚫 Closing edit form - person was updated');
                        setShowPersonForm(false);
                        setEditingPerson(null);
                    }

                    // Если мы просматриваем эту персону, обновляем данные
                    if (selectedPerson && selectedPerson.id === personId) {
                        console.log('🔄 Refreshing person view');
                        // Загружаем обновленные данные
                        personService.getById(personId)
                            .then(response => setSelectedPerson(response.data))
                            .catch(error => console.error('Error refreshing person view:', error));
                    }
                    break;

                case 'DELETED':
                    // Закрываем формы если удалена редактируемая/просматриваемая персона
                    if ((editingPerson && editingPerson.id === personId) ||
                        (selectedPerson && selectedPerson.id === personId)) {
                        console.log('🚫 Closing modals - person was deleted');
                        setShowPersonForm(false);
                        setShowPersonView(false);
                        setEditingPerson(null);
                        setSelectedPerson(null);
                    }
                    break;
            }

            // Сообщаем, что обработали обновление
            onPersonUpdateProcessed();
        }
    }, [personUpdateData, editingPerson, selectedPerson, onPersonUpdateProcessed]);


    const handleSearch = (term, field) => {
        setSearchTerm(term);
        setSearchField(field);
        // console.log(term === '');
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
                // alert('Person deleted successfully');
            } catch (error) {
                // alert('Error deleting person: ' + error.message);
            }
        }
    };

    const handleSavePerson = async (savedPerson) => {
        setShowPersonForm(false);
        setEditingPerson(null);
        await loadPersons();
    };

    const handleCancelForm = () => {
        setShowPersonForm(false);
        setEditingPerson(null);
    };


    return (
        <div className="persons-page">
            <div className="page-header">
                <h2>Person Management</h2>
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
        </div>
    )
}

export default PersonPage;