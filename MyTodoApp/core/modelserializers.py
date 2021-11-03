from rest_framework import serializers
from core.models import TodoModel


class TodoModelSerializer(serializers.ModelSerializer):
    class Meta:
        model = TodoModel
        fields = '__all__'
