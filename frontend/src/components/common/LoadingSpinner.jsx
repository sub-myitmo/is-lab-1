import React from 'react';
import '../styles/LoadingSpinner.css';

const LoadingSpinner = ({ size = 'medium', text = 'Loading...' }) => {
    return (
        <div className={`loading-spinner ${size}`}>
            <div className="spinner"></div>
            <p>{text}</p>
        </div>
    );
};

export default LoadingSpinner;