#Taller de Programación II

##Descripción

Aplicación de Android que al registrarse permite encontrar personas dependiendo del lugar, intereses y genero. Descarga imágenes de perfil y los intereses que de acuerdo a estos el usuario podrá elegir si es candidato o no, cuando ambos se hayan elegido como candidato se producirá un Match, que les permitirá empezar una conversación entre ellos.

##Requerimientos mínimos:

- Android 4.1 Jelly Bean o superior.
- Google Play Store instalada.
- Google Play services
- API 18 o superior. 

##Instrucciones para compilar:
		
- Posicionarse en la carpeta del proyecto, ejecutar ./gradlew build

##Instrucciones para generar la aplicacion firmada .apk:
		
- Posicionarse en la carpeta del proyecto, ejecutar ./gradlew assembleRelease

##Instrucciones para ver la documentacion extraida por Doxygen:

- Para ver el html, ir a la carpeta html del proyecto y buscar el archivo index.html y abrirlo con algun Navegador e ir a la pestaña Classes.
- Para ver el Latex generado, ir a la carpeta latex y buscar el archivo refman.pdf .
	
##Funcionalidad incluida:
	
- Pantalla de bienvenida (SplashScreen)
- Pantalla para ingresar ip:port del server.
- Pantalla de Selección de Registro o Login.
- Pantalla de Registro y la lógica relaciona con el registro de usuarios.
- Pantalla de Intereses se agregan intereses durante el registro, pueden ser múltiples en una misma categoría.
- Pantalla de Logueo y la lógica asociada a esta.
- Pantalla de Macheo llegan notificaciones de nuevos mensajes.
- Pantalla completa de la imagen del usuario a matchear.
- Pantalla de una Lista de Chats con las personas que matchearon. (Incluye foto de perfil, nombre de usuario y ultimo mensaje)
- Pantalla de Chat y su lógica asociada con respecto a envío de mensajes, recepción de mensajes nuevos y carga de mensajes anteriores en el historial.
- Descarga de Información del usuario, mensajes. 

##Issues solucionados para la entrega 3:

- Crear activity de edicion de perfil. 
- Crear pantalla vista de perfil.
- Agregar una activity para ver el perfil de un usuario o el propio. 
- Agregar nuevos matches en chatList. 
- Agregar informacion del usuario en profileActivity.
- Señalizar el fallo de envio de mensaje con burbuja gris.
- Enviar el mensaje mal enviado con un click. 
- Reenviar el mensaje que no se pudo mandar.
- Mostrar mensajes enviados rapidamente en el chat. 
- Enviar posicion actual.  
- Enviar imagen de usuario. 
- enviar tokenGCM al server. 
- guardar los matches (nombres de usuarios) internamente. 
- Chatlist: pedir fotos. 
- Agregar intereces a el registro. 
- Agregar funcionalidad a los botones like y dislike. 
- Imagenes Base64 http.
- Imagenes grandes crash.
- problemas sugerencias en intereses.
- Algunos Datos persistidos no se borran correctamente.

	
##Issues solucionados para la entrega 2:
- Chat con GCM.
- Sincronizar singleton.
- Borrar notificaciones al ver los mensajes.
- No actualiza listView en ChatListActivity (bug).
- Actualizar ChatListActivity al salir de ChatActivity. 
- Agregar una sola selección para RadioButton hombre - mujer.
- Darle el nombre al Chat de la persona con la que estas hablando.
- Agregar últimos mensajes a las notificaciones y cantidad de mensajes. 
- Correcciones Checkpoint 1.

##Issues solucionados para la entrega 1:

- Registro: Crear pantalla de registro. 
- Lógica de envío de información. 
- Lógica de recepción de información. 
- Descarga de imágenes por http. 




