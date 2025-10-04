import React from 'react';
import '../styles/Pagination.css';

const Pagination = ({
                        currentPage,
                        totalPages,
                        onPageChange,
                        pageSize,
                        onPageSizeChange,
                        totalItems
                    }) => {
    const pages = [];
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(currentPage + totalPages - 1, currentPage + 2);

    for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
    }

    return (
        <div className="pagination">
            <div className="pagination-info">
                Showing {currentPage * pageSize + 1} - {Math.min((currentPage + 1) * pageSize, totalItems)} of {totalItems}
            </div>

            <div className="pagination-controls">
                <button
                    onClick={() => onPageChange(0)}
                    disabled={currentPage === 0}
                >
                    First
                </button>

                <button
                    onClick={() => onPageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                >
                    Previous
                </button>

                {pages.map(page => (
                    <button
                        key={page}
                        onClick={() => onPageChange(page)}
                        className={currentPage === page ? 'active' : ''}
                    >
                        {page + 1}
                    </button>
                ))}

                <button
                    onClick={() => onPageChange(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                >
                    Next
                </button>

                <button
                    onClick={() => onPageChange(totalPages - 1)}
                    disabled={currentPage >= totalPages - 1}
                >
                    Last
                </button>
            </div>

            <div className="page-size-selector">
                <label>Items per page:</label>
                <select
                    value={pageSize}
                    onChange={(e) => onPageSizeChange(Number(e.target.value))}
                >
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                </select>
            </div>
        </div>
    );
};

export default Pagination;