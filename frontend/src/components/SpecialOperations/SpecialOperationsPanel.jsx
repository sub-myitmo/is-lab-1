import React, { useState } from 'react';
import { Color, Country } from '../../utils/constants';
import '../styles/SpecialOperations.css';
import operationService from "../../services/operationsService.js";

const SpecialOperationsPanel = () => {
    const [operationResult, setOperationResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [selectedNationality, setSelectedNationality] = useState('');
    const [selectedHairColor, setSelectedHairColor] = useState('');
    const [selectedEyeColor, setSelectedEyeColor] = useState('');

    const executeOperation = async (operation) => {
        setLoading(true);
        setOperationResult(null);

        try {
            let result;
            switch (operation) {
                case 'minPassport':
                    result = await operationService.getMinPassport();
                    setOperationResult({
                        type: 'Person with Min Passport',
                        data: result.data
                    });
                    break;

                case 'nationalityLessThan':
                    if (!selectedNationality) {
                        alert('Please select nationality');
                        return;
                    }
                    result = await operationService.countNationalityLessThan(selectedNationality);
                    setOperationResult({
                        type: `Count where nationality < ${selectedNationality}`,
                        data: result.data.count
                    });
                    break;

                case 'nationalityGreaterThan':
                    if (!selectedNationality) {
                        alert('Please select nationality');
                        return;
                    }
                    result = await operationService.countNationalityGreaterThan(selectedNationality);
                    setOperationResult({
                        type: `Count where nationality > ${selectedNationality}`,
                        data: result.data.count
                    });
                    break;

                case 'hairColor':
                    if (!selectedHairColor) {
                        alert('Please select hair color');
                        return;
                    }
                    result = await operationService.countByHairColor(selectedHairColor);
                    setOperationResult({
                        type: `Count with hair color ${selectedHairColor}`,
                        data: result.data.count
                    });
                    break;

                case 'eyeColor':
                    if (!selectedEyeColor) {
                        alert('Please select eye color');
                        return;
                    }
                    result = await operationService.countByEyeColor(selectedEyeColor);
                    setOperationResult({
                        type: `Count with eye color ${selectedEyeColor}`,
                        data: result.data.count
                    });
                    break;

                default:
                    break;
            }
        } catch (error) {
            console.error('Error executing operation:', error);
            setOperationResult({
                type: 'Error',
                data: error.response?.data?.error || error.message
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="special-operations-panel">
            <h2>Special Operations</h2>

            <div className="operations-grid">
                <div className="operation-card">
                    <h3>Find Min Passport ID</h3>
                    <p>Returns the person with the smallest passport ID</p>
                    <button
                        onClick={() => executeOperation('minPassport')}
                        disabled={loading}
                    >
                        Execute
                    </button>
                </div>

                <div className="operation-card">
                    <h3>Count by Nationality (Less Than)</h3>
                    <select
                        value={selectedNationality}
                        onChange={(e) => setSelectedNationality(e.target.value)}
                    >
                        <option value="">Select Nationality</option>
                        {Object.values(Country).map(country => (
                            <option key={country} value={country}>{country}</option>
                        ))}
                    </select>
                    <button
                        onClick={() => executeOperation('nationalityLessThan')}
                        disabled={loading || !selectedNationality}
                    >
                        Execute
                    </button>
                </div>

                <div className="operation-card">
                    <h3>Count by Nationality (Greater Than)</h3>
                    <select
                        value={selectedNationality}
                        onChange={(e) => setSelectedNationality(e.target.value)}
                    >
                        <option value="">Select Nationality</option>
                        {Object.values(Country).map(country => (
                            <option key={country} value={country}>{country}</option>
                        ))}
                    </select>
                    <button
                        onClick={() => executeOperation('nationalityGreaterThan')}
                        disabled={loading || !selectedNationality}
                    >
                        Execute
                    </button>
                </div>

                <div className="operation-card">
                    <h3>Count by Hair Color</h3>
                    <select
                        value={selectedHairColor}
                        onChange={(e) => setSelectedHairColor(e.target.value)}
                    >
                        <option value="">Select Hair Color</option>
                        {Object.values(Color).map(color => (
                            <option key={color} value={color}>{color}</option>
                        ))}
                    </select>
                    <button
                        onClick={() => executeOperation('hairColor')}
                        disabled={loading || !selectedHairColor}
                    >
                        Execute
                    </button>
                </div>

                <div className="operation-card">
                    <h3>Count by Eye Color</h3>
                    <select
                        value={selectedEyeColor}
                        onChange={(e) => setSelectedEyeColor(e.target.value)}
                    >
                        <option value="">Select Eye Color</option>
                        {Object.values(Color).map(color => (
                            <option key={color} value={color}>{color}</option>
                        ))}
                    </select>
                    <button
                        onClick={() => executeOperation('eyeColor')}
                        disabled={loading || !selectedEyeColor}
                    >
                        Execute
                    </button>
                </div>
            </div>

            {loading && (
                <div className="operation-loading">
                    <p>Executing operation...</p>
                </div>
            )}

            {operationResult && (
                <div className="operation-result">
                    <h3>Operation Result: {operationResult.type}</h3>
                    <pre>{JSON.stringify(operationResult.data, null, 2)}</pre>
                </div>
            )}
        </div>
    );
};

export default SpecialOperationsPanel;