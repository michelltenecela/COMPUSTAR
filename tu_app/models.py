from django.db import models
    
class Trabajador(models.Model):
    id_trabajador = models.AutoField(primary_key=True)
    email = models.CharField(max_length=255)
    nombre = models.CharField(max_length=255)
    cedula = models.CharField(max_length=255)
    contraseña = models.CharField(max_length=255)

    def __str__(self):
        return self.nombre
    
class Area(models.Model):
    id_areatrabajo = models.AutoField(primary_key=True)
    nombre = models.CharField(max_length=255)
    estado = models.BooleanField(default=False)

    def __str__(self):
        return self.nombre
    
class AreaDetalle(models.Model):
    id_areadetalle = models.AutoField(primary_key=True)
    id_areatrabajo = models.ForeignKey(Area, on_delete=models.CASCADE)
    id_trabajador = models.ForeignKey(Trabajador, on_delete=models.CASCADE)

    def __str__(self):
        return self.id_areadetalle
    
class Cliente(models.Model):
    id_cliente = models.AutoField(primary_key=True)
    nombre = models.CharField(max_length=255)
    cedula = models.CharField(max_length=255)
    telefono = models.CharField(max_length=255)

    def __str__(self):
        return self.nombre

class Equipo(models.Model):
    id_equipo = models.AutoField(primary_key=True)
    id_cliente = models.ForeignKey(Cliente, on_delete=models.CASCADE)
    id_trabajador = models.ForeignKey(Trabajador, on_delete=models.CASCADE)
    n_ingreso = models.CharField(max_length=255)
    equipo = models.CharField(max_length=255)
    n_serie = models.CharField(max_length=255)
    marca = models.CharField(max_length=255)
    modelo = models.CharField(max_length=255)
    fecha_ingreso = models.CharField(max_length=255)
    fecha_finalizacion = models.CharField(max_length=255)
    falla = models.CharField(max_length=255)
    Obeservacion = models.CharField(max_length=255)
    estado = models.BooleanField(default=False)

    def __str__(self):
        return self.equipo
    
class Tareas(models.Model):
    id_tarea = models.AutoField(primary_key=True)
    id_equipo = models.ForeignKey(Equipo, on_delete=models.CASCADE)
    falla = models.CharField(max_length=255)
    descripcion = models.CharField(max_length=255)
    fecha_finalizacion = models.CharField(max_length=255)
    estado = models.BooleanField(default=False)

    def __str__(self):
        return str(self.falla)