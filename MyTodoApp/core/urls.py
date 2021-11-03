from django.urls import path, include
from core import views


urlpatterns = [
    path('addTodoItem', views.TodoModelAPIView.as_view(), name='addTodoItem'),
    path('', views.home, name='home'),
]