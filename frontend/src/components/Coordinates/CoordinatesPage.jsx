import CoordinatesTable from "./CoordinatesTable.jsx";
import React, {useCallback, useEffect, useState} from "react";
import personService from "../../services/mainService.js";
import LoadingSpinner from "../common/LoadingSpinner.jsx";
import Pagination from "../common/Pagination.jsx";
import CoordinatesView from "./CoordinatesView.jsx";
import CoordinatesForm from "./CoordinatesForm.jsx";

const CoordinatesPage = ({refreshTrigger, coordinatesUpdateData, onCoordinatesUpdateProcessed}) => {
    const [coordinatesArr, setCoordinatesArr] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');

    const [showCoordinatesForm, setShowCoordinatesForm] = useState(false);
    const [showCoordinatesView, setShowCoordinatesView] = useState(false);
    const [selectedCoordinates, setSelectedCoordinates] = useState(null);
    const [editingCoordinates, setEditingCoordinates] = useState(null);


    const loadCoordinatesArr = useCallback(async () => {
        setLoading(true);
        try {
            const response = await personService.getAllCoordinates(
                currentPage,
                pageSize,
                sortField,
                sortDirection
            );

            const coordinatesArrData = response.data.coordinates || [];
            setCoordinatesArr(coordinatesArrData);
            setTotalItems(response.data.totalCount || 0);

        } catch (error) {
            console.error('Error loading coordinatesArr:', error);
        } finally {
            setLoading(false);
        }
    }, [currentPage, pageSize, sortField, sortDirection]);

    // Загрузка при изменении параметров
    useEffect(() => {
        loadCoordinatesArr();
    }, [loadCoordinatesArr]);

    // Реакция на триггер обновления
    useEffect(() => {
        if (refreshTrigger > 0) {
            loadCoordinatesArr();
        }
    }, [refreshTrigger, loadCoordinatesArr]);

    // Реакция на конкретные обновления Coordinates
    useEffect(() => {
        if (coordinatesUpdateData) {
            const {coordinatesId, action} = coordinatesUpdateData;

            switch (action) {
                case 'UPDATED':
                    // Если мы редактируем эту локацию, закрываем форму
                    if (editingCoordinates && editingCoordinates.id === coordinatesId) {
                        console.log('🚫 Closing edit form - coordinates was updated');
                        setShowCoordinatesForm(false);
                        setEditingCoordinates(null);
                    }

                    // Если мы просматриваем эту локацию, обновляем данные
                    if (selectedCoordinates && selectedCoordinates.id === coordinatesId) {
                        console.log('🔄 Refreshing coordinates view');
                        // Загружаем обновленные данные
                        personService.getById(coordinatesId)
                            .then(response => setSelectedCoordinates(response.data))
                            .catch(error => console.error('Error refreshing coordinates view:', error));
                    }
                    break;

                case 'DELETED':
                    // Закрываем формы если удалена редактируемая/просматриваемая локация
                    if ((editingCoordinates && editingCoordinates.id === coordinatesId) ||
                        (selectedCoordinates && selectedCoordinates.id === coordinatesId)) {
                        console.log('🚫 Closing modals - coordinates was deleted');
                        setShowCoordinatesForm(false);
                        setShowCoordinatesView(false);
                        setEditingCoordinates(null);
                        setSelectedCoordinates(null);
                    }
                    break;
            }

            // Сообщаем, что обработали обновление
            onCoordinatesUpdateProcessed();
        }
    }, [coordinatesUpdateData, editingCoordinates, selectedCoordinates, onCoordinatesUpdateProcessed]);

    const handleSort = (field) => {
        const newSortDirection = sortField === field
            ? (sortDirection === 'asc' ? 'desc' : 'asc')
            : 'asc';

        setSortField(field);
        setSortDirection(newSortDirection);
        setCurrentPage(0);
    };

    const handleCreateCoordinates = () => {
        setEditingCoordinates(null);
        setShowCoordinatesForm(true);
    };

    const handleEditCoordinates = (coordinates) => {
        setEditingCoordinates(coordinates);
        setShowCoordinatesForm(true);
    };

    const handleViewCoordinates = (coordinates) => {
        setSelectedCoordinates(coordinates);
        setShowCoordinatesView(true);
    };

    const handleDeleteCoordinates = async (coordinates) => {
        if (window.confirm(`Are you sure you want to delete ${coordinates.name}?`)) {
            try {
                await personService.delete(coordinates.id);
                await loadCoordinatesArr();
                // alert('Coordinates deleted successfully');
            } catch (error) {
                // alert('Error deleting coordinates: ' + error.message);
            }
        }
    };

    const handleSaveCoordinates = async (savedCoordinates) => {
        setShowCoordinatesForm(false);
        setEditingCoordinates(null);
        await loadCoordinatesArr();
    };

    const handleCancelForm = () => {
        setShowCoordinatesForm(false);
        setEditingCoordinates(null);
    };


    return (
        <div className="coordinatesArr-page">
            <div className="page-header">
                <h2>Coordinates Management</h2>
                <button onClick={handleCreateCoordinates} className="btn-primary">
                    Add New Coordinates
                </button>
            </div>

            {loading ? (
                <LoadingSpinner text="Loading coordinates..."/>
            ) : (
                <>
                    <CoordinatesTable
                        coordinatesArr={coordinatesArr}
                        onEdit={handleEditCoordinates}
                        onDelete={handleDeleteCoordinates}
                        onView={handleViewCoordinates}
                        onSort={handleSort}
                        sortField={sortField}
                        sortDirection={sortDirection}
                    />

                    <Pagination
                        currentPage={currentPage}
                        totalPages={coordinatesArr.length === pageSize ? Math.ceil(totalItems / pageSize) : Math.ceil(coordinatesArr.length / pageSize)}
                        onPageChange={setCurrentPage}
                        pageSize={pageSize}
                        onPageSizeChange={setPageSize}
                        totalItems={totalItems}
                    />
                </>
            )}

            {showCoordinatesForm && (
                <div className="modal-overlay">
                    <div className="modal">
                        <CoordinatesForm
                            coordinates={editingCoordinates}
                            onSave={handleSaveCoordinates}
                            onCancel={handleCancelForm}
                            isEditing={!!editingCoordinates}
                        />
                    </div>
                </div>
            )}

            {showCoordinatesView && (
                <CoordinatesView
                    coordinates={selectedCoordinates}
                    onClose={() => setShowCoordinatesView(false)}
                    onEdit={handleEditCoordinates}
                />
            )}
        </div>
    )
}

export default CoordinatesPage;