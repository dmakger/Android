from django.db import models


# Create your models here.
class TodoModel(models.Model):
    title = models.CharField(max_length=120, blank=False, default='New todo')
    description = models.TextField(blank=True, default='')
