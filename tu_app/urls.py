from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ClienteViewSet, TrabajadorViewSet, AreaViewSet, AreaDetalleViewSet, EquipoViewSet, TareasViewSet


router = DefaultRouter()
router.register(r'cliente', ClienteViewSet)
router.register(r'trabajador', TrabajadorViewSet)
router.register(r'area', AreaViewSet)
router.register(r'area_detalle', AreaDetalleViewSet)
router.register(r'equipo', EquipoViewSet)
router.register(r'tarea', TareasViewSet)

urlpatterns = [
    path('api/', include(router.urls)),
]

