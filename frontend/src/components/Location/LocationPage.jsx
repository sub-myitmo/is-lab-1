import LocationTable from "./LocationTable.jsx";
import React, {useCallback, useEffect, useState} from "react";
import personService from "../../services/mainService.js";
import LoadingSpinner from "../common/LoadingSpinner.jsx";
import Pagination from "../common/Pagination.jsx";
import LocationView from "./LocationView.jsx";
import LocationForm from "./LocationForm.jsx";

const LocationPage = ({refreshTrigger, locationUpdateData, onLocationUpdateProcessed}) => {
    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');

    const [showLocationForm, setShowLocationForm] = useState(false);
    const [showLocationView, setShowLocationView] = useState(false);
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [editingLocation, setEditingLocation] = useState(null);


    const loadLocations = useCallback(async () => {
        setLoading(true);
        try {
            const response = await personService.getAllLocations(
                currentPage,
                pageSize,
                sortField,
                sortDirection
            );

            const locationsData = response.data.locations || [];
            setLocations(locationsData);
            setTotalItems(response.data.totalCount || 0);


        } catch (error) {
            console.error('Error loading locations:', error);
        } finally {
            setLoading(false);
        }
    }, [currentPage, pageSize, sortField, sortDirection]);

    // Загрузка при изменении параметров
    useEffect(() => {
        loadLocations();
    }, [loadLocations]);

    // Реакция на триггер обновления
    useEffect(() => {
        if (refreshTrigger > 0) {
            loadLocations();
        }
    }, [refreshTrigger, loadLocations]);

    // Реакция на конкретные обновления Location
    useEffect(() => {
        if (locationUpdateData) {
            const {locationId, action} = locationUpdateData;

            switch (action) {
                case 'UPDATED':
                    // Если мы редактируем эту локацию, закрываем форму
                    if (editingLocation && editingLocation.id === locationId) {
                        console.log('🚫 Closing edit form - location was updated');
                        setShowLocationForm(false);
                        setEditingLocation(null);
                    }

                    // Если мы просматриваем эту локацию, обновляем данные
                    if (selectedLocation && selectedLocation.id === locationId) {
                        console.log('🔄 Refreshing location view');
                        // Загружаем обновленные данные
                        personService.getById(locationId)
                            .then(response => setSelectedLocation(response.data))
                            .catch(error => console.error('Error refreshing location view:', error));
                    }
                    break;

                case 'DELETED':
                    // Закрываем формы если удалена редактируемая/просматриваемая локация
                    if ((editingLocation && editingLocation.id === locationId) ||
                        (selectedLocation && selectedLocation.id === locationId)) {
                        console.log('🚫 Closing modals - location was deleted');
                        setShowLocationForm(false);
                        setShowLocationView(false);
                        setEditingLocation(null);
                        setSelectedLocation(null);
                    }
                    break;
            }

            // Сообщаем, что обработали обновление
            onLocationUpdateProcessed();
        }
    }, [locationUpdateData, editingLocation, selectedLocation, onLocationUpdateProcessed]);

    const handleSort = (field) => {
        const newSortDirection = sortField === field
            ? (sortDirection === 'asc' ? 'desc' : 'asc')
            : 'asc';

        setSortField(field);
        setSortDirection(newSortDirection);
        setCurrentPage(0);
    };

    const handleCreateLocation = () => {
        setEditingLocation(null);
        setShowLocationForm(true);
    };

    const handleEditLocation = (location) => {
        setEditingLocation(location);
        setShowLocationForm(true);
    };

    const handleViewLocation = (location) => {
        setSelectedLocation(location);
        setShowLocationView(true);
    };

    const handleDeleteLocation = async (location) => {
        if (window.confirm(`Are you sure you want to delete ${location.name}?`)) {
            try {
                await personService.delete(location.id);
                await loadLocations();
                // alert('Location deleted successfully');
            } catch (error) {
                // alert('Error deleting location: ' + error.message);
            }
        }
    };

    const handleSaveLocation = async (savedLocation) => {
        setShowLocationForm(false);
        setEditingLocation(null);
        await loadLocations();
    };

    const handleCancelForm = () => {
        setShowLocationForm(false);
        setEditingLocation(null);
    };


    return (
        <div className="locations-page">
            <div className="page-header">
                <h2>Location Management</h2>
                <button onClick={handleCreateLocation} className="btn-primary">
                    Add New Location
                </button>
            </div>

            {loading ? (
                <LoadingSpinner text="Loading locations..."/>
            ) : (
                <>
                    <LocationTable
                        locations={locations}
                        onEdit={handleEditLocation}
                        onDelete={handleDeleteLocation}
                        onView={handleViewLocation}
                        onSort={handleSort}
                        sortField={sortField}
                        sortDirection={sortDirection}
                    />

                    <Pagination
                        currentPage={currentPage}
                        totalPages={locations.length === pageSize ? Math.ceil(totalItems / pageSize) : Math.ceil(locations.length / pageSize)}
                        onPageChange={setCurrentPage}
                        pageSize={pageSize}
                        onPageSizeChange={setPageSize}
                        totalItems={totalItems}
                    />
                </>
            )}

            {showLocationForm && (
                <div className="modal-overlay">
                    <div className="modal">
                        <LocationForm
                            location={editingLocation}
                            onSave={handleSaveLocation}
                            onCancel={handleCancelForm}
                            isEditing={!!editingLocation}
                        />
                    </div>
                </div>
            )}

            {showLocationView && (
                <LocationView
                    location={selectedLocation}
                    onClose={() => setShowLocationView(false)}
                    onEdit={handleEditLocation}
                />
            )}
        </div>
    )
}

export default LocationPage;