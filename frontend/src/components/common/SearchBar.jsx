import React, {useEffect, useState} from 'react';
import {debounce} from '../../utils/helpers';
import '../styles/SearchBar.css';
import {FIELDS} from "../../utils/constants.js";

const SearchBar = ({onSearch, term, field}) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchField, setSearchField] = useState('name');


    useEffect(() => {
        setSearchTerm(term);
        setSearchField(field);
    }, [term, field]);

    const debouncedSearch = debounce((value, field) => {
        onSearch(value, field);
    }, 300);

    const handleChange = (e) => {
        const value = e.target.value;
        setSearchTerm(value);
        debouncedSearch(value, searchField);
    };

    const handleChangeField = (e) => {
        const value = e.target.value;
        setSearchField(value);
        debouncedSearch(searchTerm, value);
    };

    const handleClear = () => {
        setSearchTerm('');
        onSearch('', searchField);
    };

    return (
        <div className="search-bar">
            <div className="search-input-container">
                <input
                    type="text"
                    placeholder={"Search by " + searchField + "..."}
                    value={searchTerm}
                    onChange={handleChange}
                    className="search-input"
                />
                {searchTerm && (
                    <button onClick={handleClear} className="clear-button">
                        ×
                    </button>
                )}

                <div className="custom-select">
                    <select
                        value={searchField}
                        onChange={(e) => handleChangeField(e)}
                    >
                        {FIELDS
                            .filter(field => field.filterable === true)
                            .map(field => (
                                <option key={field.key} value={field.key}>
                                    {field.label}
                                </option>
                            ))}
                    </select>
                </div>
            </div>
        </div>
    );
};

export default SearchBar;