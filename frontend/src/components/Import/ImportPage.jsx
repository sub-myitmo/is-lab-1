import ImportTable from "./ImportTable.jsx";
import React, {useCallback, useEffect, useState, useRef} from "react";
import personService from "../../services/mainService.js";
import LoadingSpinner from "../common/LoadingSpinner.jsx";
import Pagination from "../common/Pagination.jsx";
import ImportView from "./ImportView.jsx";

const ImportPage = ({refreshTrigger}) => {
    const [userImports, setUserImports] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');

    const [showUserImportView, setShowUserImportView] = useState(false);
    const [selectedUserImport, setSelectedUserImport] = useState(null);

    const [importLoading, setImportLoading] = useState(false);
    const fileInputRef = useRef(null);

    const loadUserImports = useCallback(async () => {
        setLoading(true);
        try {
            const response = await personService.getAllImports(
                currentPage,
                pageSize,
                sortField,
                sortDirection
            );

            const userImportsData = response.data.userImports || [];
            setUserImports(userImportsData);
            setTotalItems(response.data.totalCount || 0);


        } catch (error) {
            console.error('Error loading imports:', error);
        } finally {
            setLoading(false);
        }
    }, [currentPage, pageSize, sortField, sortDirection]);

    // Загрузка при изменении параметров
    useEffect(() => {
        loadUserImports();
    }, [loadUserImports]);

    // Реакция на триггер обновления
    useEffect(() => {
        if (refreshTrigger > 0) {
            loadUserImports();
        }
    }, [refreshTrigger, loadUserImports]);

    const handleSort = (field) => {
        const newSortDirection = sortField === field
            ? (sortDirection === 'asc' ? 'desc' : 'asc')
            : 'asc';

        setSortField(field);
        setSortDirection(newSortDirection);
        setCurrentPage(0);
    };

    const handleViewImport = (userImport) => {
        setSelectedImport(userImport);
        setShowImportView(true);
    };


    const handleImportJson = useCallback(async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.name.toLowerCase().endsWith('.json')) {
            alert('Пожалуйста, выберите JSON файл');
            return;
        }

        setImportLoading(true);
        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await personService.importData(formData);

            console.log('Результат импорта:', response.data);

        } catch (error) {
            console.error('Ошибка при импорте файла:', error);
        } finally {
            setImportLoading(false);
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
        }
    }, []);


    const handleImportButtonClick = () => {
        if (fileInputRef.current) {
            fileInputRef.current.click();
        }
    };


    return (
        <div className="imports-page">
            <div className="page-header">
                <h2>Imports Management</h2>
            </div>


            {loading ? (
                <LoadingSpinner text="Loading history imports..."/>
            ) : (
                <>
                    <input
                        type="file"
                        ref={fileInputRef}
                        onChange={handleImportJson}
                        accept=".json"
                    />
                    <ImportTable
                        userImports={userImports}
                        onView={handleViewImport}
                        onSort={handleSort}
                        sortField={sortField}
                        sortDirection={sortDirection}
                    />

                    <Pagination
                        currentPage={currentPage}
                        totalPages={userImports.length === pageSize ? Math.ceil(totalItems / pageSize) : Math.ceil(userImports.length / pageSize)}
                        onPageChange={setCurrentPage}
                        pageSize={pageSize}
                        onPageSizeChange={setPageSize}
                        totalItems={totalItems}
                    />
                </>
            )}

            {showUserImportView && (
                <ImportView
                    userImport={selectedUserImport}
                    onClose={() => setShowUserImportView(false)}
                />
            )}
        </div>
    )
}

export default ImportPage;