// Connection Status Manager for SafeCom
class ConnectionStatus {
    constructor() {
        this.isOnline = navigator.onLine;
        this.backendStatus = 'unknown';
        this.checkInterval = null;
        this.statusIndicator = null;
        
        this.init();
    }
    
    init() {
        this.createStatusIndicator();
        this.bindEvents();
        this.startHealthCheck();
    }
    
    createStatusIndicator() {
        // Create status indicator element
        this.statusIndicator = document.createElement('div');
        this.statusIndicator.className = 'connection-status';
        this.statusIndicator.innerHTML = `
            <div class="status-dot"></div>
            <span class="status-text">Checking connection...</span>
        `;
        
        // Add to page
        document.body.appendChild(this.statusIndicator);
        
        // Initially hidden
        this.statusIndicator.style.display = 'none';
    }
    
    bindEvents() {
        // Listen for online/offline events
        window.addEventListener('online', () => {
            this.isOnline = true;
            this.checkBackendHealth();
        });
        
        window.addEventListener('offline', () => {
            this.isOnline = false;
            this.updateStatus('offline', 'No internet connection');
        });
    }
    
    async startHealthCheck() {
        // Initial check
        await this.checkBackendHealth();
        
        // Check every 30 seconds
        this.checkInterval = setInterval(() => {
            this.checkBackendHealth();
        }, 30000);
    }
    
    async checkBackendHealth() {
        if (!this.isOnline) {
            this.updateStatus('offline', 'No internet connection');
            return;
        }
        
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 second timeout
            
            const response = await fetch(`${SafeComConfig.API_BASE}/health`, {
                method: 'GET',
                signal: controller.signal
            });
            
            clearTimeout(timeoutId);
            
            if (response.ok) {
                this.updateStatus('online', 'Connected to backend');
                this.backendStatus = 'online';
            } else {
                this.updateStatus('warning', 'Backend service issues');
                this.backendStatus = 'warning';
            }
        } catch (error) {
            if (error.name === 'AbortError') {
                this.updateStatus('warning', 'Backend response slow');
                this.backendStatus = 'slow';
            } else {
                this.updateStatus('offline', 'Backend unavailable - Demo mode available');
                this.backendStatus = 'offline';
            }
        }
    }
    
    updateStatus(status, message) {
        if (!this.statusIndicator) return;
        
        // Update classes
        this.statusIndicator.className = `connection-status ${status}`;
        this.statusIndicator.querySelector('.status-text').textContent = message;
        
        // Show indicator for issues, hide when everything is good
        if (status === 'online') {
            setTimeout(() => {
                if (this.statusIndicator) {
                    this.statusIndicator.style.display = 'none';
                }
            }, 2000);
        } else {
            this.statusIndicator.style.display = 'block';
        }
    }
    
    getStatus() {
        return {
            isOnline: this.isOnline,
            backendStatus: this.backendStatus
        };
    }
    
    destroy() {
        if (this.checkInterval) {
            clearInterval(this.checkInterval);
        }
        if (this.statusIndicator) {
            this.statusIndicator.remove();
        }
    }
}

// Initialize connection status when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.connectionStatus = new ConnectionStatus();
});

// Add CSS for connection status
const connectionStatusCSS = `
.connection-status {
    position: fixed;
    top: 20px;
    right: 20px;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    gap: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    z-index: 10000;
    font-size: 14px;
    max-width: 280px;
}

.connection-status.online {
    border-color: #10b981;
    background: #f0fdf4;
}

.connection-status.warning {
    border-color: #f59e0b;
    background: #fffbeb;
}

.connection-status.offline {
    border-color: #ef4444;
    background: #fef2f2;
}

.connection-status .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    flex-shrink: 0;
}

.connection-status.online .status-dot {
    background: #10b981;
}

.connection-status.warning .status-dot {
    background: #f59e0b;
}

.connection-status.offline .status-dot {
    background: #ef4444;
}

.connection-status .status-text {
    color: #374151;
    font-weight: 500;
}
`;

// Inject CSS
const style = document.createElement('style');
style.textContent = connectionStatusCSS;
document.head.appendChild(style);
