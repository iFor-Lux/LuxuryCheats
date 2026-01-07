/**
 * Inicialización específica para WebView de Android
 * - Establece altura del body/html para que el CSS funcione
 * - Corrige altura del canvas si es necesario
 * - Verifica que WebGL esté funcionando correctamente
 * - Permite actualizar el tamaño del canvas dinámicamente
 */

(function() {
    'use strict';
    
    // Función para actualizar el tamaño del canvas
    window.updateCanvasSize = function(width, height) {
        const canvas = document.getElementById('canvas');
        if (!canvas) {
            console.error('[WebView Init] Canvas not found');
            return;
        }
        
        const size = Math.min(width, height);
        
        // Actualizar viewport del HTML/body
        document.documentElement.style.width = width + 'px';
        document.documentElement.style.height = height + 'px';
        document.documentElement.style.overflow = 'hidden';
        
        document.body.style.width = width + 'px';
        document.body.style.height = height + 'px';
        document.body.style.margin = '0';
        document.body.style.display = 'flex';
        document.body.style.alignItems = 'center';
        document.body.style.justifyContent = 'center';
        document.body.style.overflow = 'hidden';
        
        // Actualizar tamaño del canvas
        canvas.style.width = size + 'px';
        canvas.style.height = size + 'px';
        canvas.style.maxWidth = '100%';
        canvas.style.maxHeight = '100%';
        canvas.style.display = 'block';
        canvas.style.margin = 'auto';
        
        console.log('[WebView Init] Canvas updated size:', size, 'x', size);
        console.log('[WebView Init] Viewport updated:', width, 'x', height);
    };
    
    // Esperar a que el DOM esté listo
    function initWebView() {
        // Obtener dimensiones del viewport
        const viewportWidth = window.innerWidth || document.documentElement.clientWidth;
        const viewportHeight = window.innerHeight || document.documentElement.clientHeight;
        
        // Usar la función de actualización
        window.updateCanvasSize(viewportWidth, viewportHeight);
        
        // Verificar WebGL después de un breve delay para asegurar que main.js haya inicializado
        setTimeout(function() {
            const canvas = document.getElementById('canvas');
            if (!canvas) {
                return;
            }
            
            window.gl = canvas.getContext('webgl2') || canvas.getContext('webgl') || 
                       canvas.getContext('webgl') || 
                       canvas.getContext('experimental-webgl');
            
            if (!gl) {
                console.error('[WebView Init] WebGL not available');
                return;
            }
            
            console.log('[WebView Init] WebGL available:', gl.getParameter(gl.VERSION));
            console.log('[WebView Init] Canvas internal size:', canvas.width, 'x', canvas.height);
            console.log('[WebView Init] Canvas style size:', canvas.style.width, 'x', canvas.style.height);
            console.log('[WebView Init] Body size:', document.body.clientWidth, 'x', document.body.clientHeight);
            
            // Verificar dimensiones finales del bounding rect
            const finalRect = canvas.getBoundingClientRect();
            console.log('[WebView Init] Canvas bounding rect:', finalRect.width, 'x', finalRect.height);
            
            if (finalRect.width > 0 && finalRect.height > 0) {
                console.log('[WebView Init] Canvas is visible and properly sized');
            } else {
                console.warn('[WebView Init] Canvas dimensions still incorrect, forcing size...');
                // Forzar tamaño si aún no está correcto
                if (finalRect.height === 0 && finalRect.width > 0) {
                    canvas.style.height = finalRect.width + 'px';
                    console.log('[WebView Init] Force set height to:', canvas.style.height);
                }
            }
        }, 200);
    }
    
    // Ejecutar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initWebView);
    } else {
        initWebView();
    }
})();

