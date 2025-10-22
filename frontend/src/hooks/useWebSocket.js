// hooks/useWebSocket.js
import { useEffect, useRef, useState, useCallback } from 'react';

export const useWebSocket = (url, onMessage) => {
    const [isConnected, setIsConnected] = useState(false);
    const ws = useRef(null);
    const reconnectTimeoutRef = useRef(null);

    const connect = useCallback(() => {
        // Если уже подключены или в процессе подключения - выходим
        if (ws.current && (ws.current.readyState === WebSocket.OPEN || ws.current.readyState === WebSocket.CONNECTING)) {
            return;
        }

        try {
            console.log('🔗 Connecting to WebSocket...', url);

            ws.current = new WebSocket(url);

            ws.current.onopen = () => {
                console.log('✅ WebSocket connected');
                setIsConnected(true);
                // Очищаем таймер переподключения если он был
                if (reconnectTimeoutRef.current) {
                    clearTimeout(reconnectTimeoutRef.current);
                    reconnectTimeoutRef.current = null;
                }
            };

            ws.current.onclose = (event) => {
                console.log('❌ WebSocket disconnected:', event.code, event.reason);
                setIsConnected(false);

                // Автопереподключение только если это не нормальное закрытие
                if (event.code !== 1000) { // 1000 = нормальное закрытие
                    console.log('🔄 Will reconnect in 3 seconds...');
                    reconnectTimeoutRef.current = setTimeout(() => {
                        connect();
                    }, 3000);
                }
            };

            ws.current.onmessage = (event) => {
                console.log('📨 WebSocket message received:', event.data);
                if (onMessage) {
                    onMessage(event.data);
                }
            };

            ws.current.onerror = (error) => {
                console.error('💥 WebSocket error:', error);
            };

        } catch (error) {
            console.error('💥 WebSocket connection error:', error);
        }
    }, [url, onMessage]);

    useEffect(() => {
        connect();

        return () => {
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
            if (ws.current) {
                console.log('🧹 Cleaning up WebSocket');
                ws.current.close(1000, 'Component unmounted');
            }
        };
    }, [connect]);

    useEffect(() => {
        let pingInterval;

        if (isConnected && ws.current) {
            // Отправляем ping каждые 30 секунд
            pingInterval = setInterval(() => {
                if (ws.current.readyState === WebSocket.OPEN) {
                    ws.current.send(JSON.stringify({ type: "ping" }));
                    console.log("📡 Sent ping");
                }
            }, 30000);
        }

        return () => {
            if (pingInterval) clearInterval(pingInterval);
        };
    }, [isConnected]);

    return { isConnected };
};