// hooks/useWebSocket.js
import { useEffect, useRef, useState, useCallback } from 'react';

export const useWebSocket = (url) => {
    const [isConnected, setIsConnected] = useState(false);
    const [lastMessage, setLastMessage] = useState(null);
    const ws = useRef(null);

    const connect = useCallback(() => {
        try {
            console.log('🔗 Connecting to WebSocket...');

            ws.current = new WebSocket(url);

            ws.current.onopen = () => {
                console.log('✅ WebSocket connected');
                setIsConnected(true);
            };

            ws.current.onclose = (event) => {
                console.log('❌ WebSocket disconnected:', event.code, event.reason);
                setIsConnected(false);

                // Автопереподключение
                setTimeout(() => {
                    if (!ws.current || ws.current.readyState === WebSocket.CLOSED) {
                        console.log('🔄 Reconnecting...');
                        connect();
                    }
                }, 3000);
            };

            ws.current.onmessage = (event) => {
                console.log('📨 WebSocket message:', event.data);
                setLastMessage(event.data);
            };

            ws.current.onerror = (error) => {
                console.error('💥 WebSocket error:', error);
            };
        } catch (error) {
            console.error('💥 WebSocket connection error:', error);
        }
    }, [url]);

    useEffect(() => {
        connect();

        return () => {
            if (ws.current) {
                ws.current.close(1000, 'Component unmounted');
            }
        };
    }, [connect]);

    return { isConnected, lastMessage };
};