from django.shortcuts import render
from rest_framework import viewsets
from .models import Cliente, Trabajador, Area, AreaDetalle, Equipo, Tareas
from .serializers import ClienteSerializer, TrabajadorSerializer, AreaSerializer, AreaDetalleSerializer, EquipoSerializer, TareasSerializer

class ClienteViewSet(viewsets.ModelViewSet):
    queryset = Cliente.objects.all()
    serializer_class = ClienteSerializer

class TrabajadorViewSet(viewsets.ModelViewSet):
    queryset = Trabajador.objects.all()
    serializer_class = TrabajadorSerializer

class AreaViewSet(viewsets.ModelViewSet):
    queryset = Area.objects.all()
    serializer_class = AreaSerializer

class AreaDetalleViewSet(viewsets.ModelViewSet):
    queryset = AreaDetalle.objects.all()
    serializer_class = AreaDetalleSerializer

class EquipoViewSet(viewsets.ModelViewSet):
    queryset = Equipo.objects.all()
    serializer_class = EquipoSerializer

class TareasViewSet(viewsets.ModelViewSet):
    queryset = Tareas.objects.all()
    serializer_class = TareasSerializer
