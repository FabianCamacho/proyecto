
drop database if exists super_Sting;
drop user if exists usuario1;
drop user if exists usuario_reportes;

-- Creación del esquema
CREATE database super_Sting
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creación de usuarios con contraseñas seguras (idealmente asignadas fuera del script)
create user 'usuario1'@'%' identified by 'contrasena';
create user 'usuario_reportes'@'%' identified by 'Usuar1o_Reportes.';

-- Asignación de permisos
-- Se otorgan permisos específicos en lugar de todos los permisos a todas las tablas futuras
grant select, insert, update, delete on super_sting.* to 'usuario1'@'%';
grant select on superSting.* to 'usuario_reportes'@'%';
flush privileges;

use super_Sting;

-- --- Sección de Creación de Tablas ---

-- Tabla de categorías
create table categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(50) NOT NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria),
  unique (descripcion),
  index ndx_descripcion (descripcion))
  ENGINE = InnoDB;

-- Tabla de productos
create table producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  descripcion VARCHAR(50) NOT NULL,  
  detalle text, 
  precio decimal(12,2) CHECK (precio >= 0),
  existencias int unsigned CHECK (existencias >= 0),
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  unique (descripcion),
  index ndx_descripcion (descripcion),
  foreign key fk_producto_categoria (id_categoria) references categoria(id_categoria))
  ENGINE = InnoDB;

-- Tabla de usuarios
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username varchar(30) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  index ndx_username (username))
  ENGINE = InnoDB;

-- Tabla de facturas
create table factura (
  id_factura INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
  total decimal(12,2) check (total>0),
  estado ENUM('Activa', 'Pagada', 'Anulada') NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_factura),  
  index ndx_id_usuario (id_usuario),
  foreign key fk_factura_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de ventas
create table venta (
  id_venta INT NOT NULL AUTO_INCREMENT,
  id_factura INT NOT NULL,
  id_producto INT NOT NULL,
  precio_historico decimal(12,2) check (precio_historico>= 0), 
  cantidad int unsigned check (cantidad> 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_venta),
  index ndx_factura (id_factura),
  index ndx_producto (id_producto),
  UNIQUE (id_factura, id_producto),
  foreign key fk_venta_factura (id_factura) references factura(id_factura),
  foreign key fk_venta_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- Tabla de roles
create table rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(20) unique,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (id_rol))
  ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
create table usuario_rol (
  id_usuario int not null,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario,id_rol),
  foreign key fk_usuarioRol_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_usuarioRol_rol (id_rol) references rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de rutas
CREATE TABLE ruta (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT NULL,
    requiere_rol boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    check (id_rol IS NOT NULL OR requiere_rol = FALSE),
    PRIMARY KEY (id_ruta),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol))
    ENGINE = InnoDB;

-- Tabla de constantes de la aplicación
CREATE TABLE constante (
    id_constante INT AUTO_INCREMENT NOT NULL,
    atributo VARCHAR(25) NOT NULL,
    valor VARCHAR(150) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_constante),
    UNIQUE (atributo))
    ENGINE = InnoDB;

-- --- Sección de Inserción de Datos ---
-- Inserción de usuarios
INSERT INTO usuario (username,password,nombre, apellidos, correo, telefono,ruta_imagen,activo) VALUES 
('juan','12345','Juan', 'Castro Mora',    'jcastro@gmail.com',    '4556-8978', 'https://img2.rtve.es/i/?w=1600&i=1677587980597.jpg',true),
('rebeca','123456','Rebeca',  'Contreras Mora', 'acontreras@gmail.com', '5456-8789','https://media.licdn.com/dms/image/v2/C5603AQGwjJ5ht4bWXQ/profile-displayphoto-shrink_200_200/profile-displayphoto-shrink_200_200/0/1661476259292?e=2147483647&v=beta&t=9_i5zTdqHRMSXlb9H4TuWkWeRGQXmaZLjxkBlWsg2lg',true),
('pedro','1234567','Pedro', 'Mena Loria',     'lmena@gmail.com',      '7898-8936','https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Eduardo_de_Pedro_2019.jpg/480px-Eduardo_de_Pedro_2019.jpg?20200109230854',true);

-- Inserción de categorias
INSERT INTO categoria (descripcion,ruta_imagen,activo) VALUES 
('Frutas y verduras', 'https://petitfitbycris.com/wp-content/uploads/2019/09/frutas-y-verduras.jpg',   true), 
('Lacteos',  'https://clubdelicatessen.com/wp-content/uploads/2021/05/productos-lacteos.jpg',   true),
('Bebidas','https://www.reddit.com/media?url=https%3A%2F%2Fi.redd.it%2Fyyr6vtruhzbb1.jpg',true),
('Panaderia','https://thefoodtech.com/wp-content/uploads/2023/10/PANADERIA-PRINCIPAL-1.jpg',    false),
('Snacks','https://m.media-amazon.com/images/I/91yD-ngFa5L._SL1500_.jpg',    false),
('Limpieza','https://m.media-amazon.com/images/I/91yD-ngFa5L._SL1500_.jpg',    false),
('Higiene','https://nadro.mx/wp-content/uploads/2022/10/Pruductos-de-higiene.jpg',    false),
('Abarrotes','https://comerciante.lacuarta.com/wp-content/uploads/2023/04/Tema-02-abarrotes.jpg',    false);

-- Inserción de productos
INSERT INTO producto (id_categoria, descripcion, detalle, precio, existencias, ruta_imagen, activo) 
VALUES 
-- Frutas y verduras (id_categoria: 1)
(1, 'Manzana Gala', 'Bolsa de 1kg de manzanas rojas frescas', 1650.00, 45, 'https://walmartcr.vtexassets.com/arquivos/ids/1079573/manzana-gala-unidad-0000000072793.jpg?v=639087732733600000', TRUE),
(1, 'Plátano Maduro', 'Racimo de plátanos de seda (aprox. 5 unidades)', 750.00, 60, 'https://www.laylita.com/recetas/wp-content/uploads/Platanos-maduros-para-freir1.jpg', TRUE),
(1, 'Aguacate Hass', 'Malla con 3 unidades de aguacate de primera', 2400.00, 20, 'https://elmundo.cr/wp-content/uploads/2022/05/Aguacate-Hass-Mexico-768x456.jpg', TRUE),

-- Lácteos (id_categoria: 2)
(2, 'Leche Semidescremada 1L', 'Caja de leche de corta duración 2% grasa', 950.00, 120, 'https://walmartcr.vtexassets.com/arquivos/ids/752345/8969_03.jpg?v=638654984512670000', TRUE),
(2, 'Queso Cheddar Tajado', 'Paquete de 200g de queso cheddar para sándwich', 1850.00, 35, 'https://d31f1ehqijlcua.cloudfront.net/n/8/a/f/7/8af74a9530a2f17e57e980bcd088e5a091baa04a_DairyandEggs_29046_01.jpg', TRUE),
(2, 'Yogurt Griego Natural', 'Envase familiar de 900g sin azúcar añadida', 2800.00, 25, 'https://walmartcr.vtexassets.com/arquivos/ids/752339/7355_01.jpg?v=638654984472230000', TRUE),

-- Bebidas (id_categoria: 3)
(3, 'Refresco de Cola 2L', 'Botella de plástico no retornable', 1450.00, 80, 'https://media.nidux.net/pull/800/800/13407/2428-product-640bad594a251-050617.png', TRUE),
(3, 'Agua Mineral Gasificada', 'Botella de 600ml de agua de manantial', 650.00, 150, 'https://walmartcr.vtexassets.com/arquivos/ids/502317/Agua-Gasificada-San-Pellegrino-1000Ml-1-27480.jpg?v=638415032793300000', TRUE),
(3, 'Jugo de Naranja 1L', 'Jugo 100% natural pasteurizado con pulpa', 1700.00, 40, 'https://walmartcr.vtexassets.com/arquivos/ids/532287/Jugo-Dos-Pinos-Naranja-1800ml-1-25559.jpg?v=638422798429500000', TRUE),

-- Panadería (id_categoria: 4)
(4, 'Pan de Molde Blanco', 'Paquete grande de 550g para tostadas', 1350.00, 50, 'https://santaisabel.vtexassets.com/arquivos/ids/451302/Pan-Molde-Ideal-Blanco-Sandwich-700-g.jpg?v=638659387991070000', TRUE),
(4, 'Baguette Artesanal', 'Pan crujiente horneado el mismo día', 600.00, 30, 'https://www.marialunarillos.com/blog/wp-content/uploads/2015/04/baguettes-1.jpg', TRUE),

-- Snacks (id_categoria: 5)
(5, 'Papas Fritas Saladas', 'Bolsa tamaño familiar de 180g', 1150.00, 95, 'https://walmartcr.vtexassets.com/arquivos/ids/951211/6142_01.jpg?v=638859678150730000', TRUE),
(5, 'Barra de Chocolate Amargo', 'Chocolate 70% cacao de 100g', 1500.00, 65, 'https://walmartcr.vtexassets.com/arquivos/ids/1045758/chocolate-nestle-classic-semi-amargo-80-g-7891000368572.webp?v=639038703067700000', TRUE),
(5, 'Mezcla de Frutos Secos', 'Bolsa de 150g con almendras, nueces y pasas', 2100.00, 40, 'https://walmartcr.vtexassets.com/arquivos/ids/834241/46707_01.jpg?v=638718899723830000', TRUE),

-- Limpieza (id_categoria: 6)
(6, 'Detergente Líquido 3L', 'Para ropa blanca y de color, rinde 30 lavadas', 5400.00, 28, 'https://walmartcr.vtexassets.com/arquivos/ids/753082/63342_02.jpg?v=638654996593530000', TRUE),
(6, 'Desinfectante Multiusos', 'Líquido aroma lavanda de 1L para pisos', 1300.00, 60, 'https://walmartcr.vtexassets.com/arquivos/ids/470906/Desinfectante-Suli-L-quido-Aroma-Manzana-Verde-3500ml-1-81052.jpg?v=638340503385270000', TRUE),

-- Higiene (id_categoria: 7)
(7, 'Jabón de Barra Avena', 'Pack de 3 unidades de 100g para piel sensible', 1600.00, 45, 'https://walmartcr.vtexassets.com/arquivos/ids/615937/Jabon-Grisi-Avena-3pk-300gr-1-103678.jpg?v=638521833908970000', TRUE),
(7, 'Pasta Dental Triple Acción', 'Tubo de 75ml con flúor y menta fresca', 1100.00, 75, 'https://walmartcr.vtexassets.com/arquivos/ids/886737/6974_01.jpg?v=638778408026430000', TRUE),

-- Abarrotes (id_categoria: 8)
(8, 'Arroz Grano Entero 1kg', 'Arroz blanco seleccionado 99% grano entero', 1400.00, 110, 'https://walmartcr.vtexassets.com/arquivos/ids/951567/53672_02.jpg?v=638859681827230000', TRUE),
(8, 'Pasta Spaghetti 500g', 'Pasta de sémola de trigo duro', 650.00, 130, 'https://walmartcr.vtexassets.com/arquivos/ids/1085738/pasta-espagueti-roma-no-5-250-g-0731701015412.webp?v=639098742069800000', TRUE);

-- Inserción de facturas
INSERT INTO factura (id_usuario,fecha,total,estado) VALUES
(1,'2025-06-05',211560,'Pagada'),
(2,'2025-06-07',554340,'Pagada'),
(3,'2025-07-07',871000,'Pagada'),
(1,'2025-07-15',244140,'Pagada'),
(2,'2025-07-17',414800,'Pagada'),
(3,'2025-07-21',420000,'Pagada');

-- Inserción de ventas
INSERT INTO venta (id_factura,id_producto,precio_historico,cantidad) values
(1,5,45000,3),
(1,9,15780,2),
(1,10,15000,3),
(2,5,45000,1),
(2,14,154000,3),
(2,9,15780,3),
(3,14,154000,1),
(3,6,57000,1),
(3,15,330000,2),
(4,6,57000,2),
(4,8,27600,3),
(4,9,15780,3),
(5,8,27600,3),
(5,14,154000,2),
(5,3,24000,1),
(6,15,330000,1),
(6,12,45000,1),
(6,10,15000,3);

-- Inserción de roles
insert into rol (rol) values ('ADMIN'), ('VENDEDOR'), ('USER');

-- ASignación de roles a usuarios
insert into usuario_rol (id_usuario, id_rol) values
 (1,1), (1,2), (1,3),(2,2),(2,3),(3,3);

-- Inserción de rutas con roles específicos
INSERT INTO ruta (ruta, id_rol) VALUES 
('/producto/nuevo', 1),
('/producto/guardar', 1),
('/producto/modificar/**', 1),
('/producto/eliminar/**', 1),
('/categoria/nuevo', 1),
('/categoria/guardar', 1),
('/categoria/modificar/**', 1),
('/categoria/eliminar/**', 1),
('/usuario/**', 1),
('/constante/**', 1),
('/role/**', 1),
('/usuario_role/**', 1),
('/ruta/**', 1),
('/producto/listado', 2),
('/categoria/listado', 2),
('/pruebas/**', 2),
('/reportes/**', 2),
('/paypal/**', 3),
('/facturar/carrito', 3);

-- Inserción de rutas que no requieren rol
INSERT INTO ruta (ruta,requiere_rol) VALUES 
('/',false),
('/index',false),
('/errores/**',false),
('/carrito/**',false),
('/registro/**',false),
('/403',false),
('/fav/**',false),
('/js/**',false),
('/css/**',false),
('/webjars/**',false);

-- Inserción de constantes de la aplicación
INSERT INTO constante (atributo,valor) VALUES 
('dominio','localhost'),
('dolar','520.75'),
('paypal.client-id','AaDNEUcELb-wQi6_MOboN0a1Ug4BnD4Z2T2-_KIoDjIb8Rif6525nvRhDu-MS-YdKQ8PJqZi7R6T6e_k'),
('paypal.client-secret','EKBpJ1oXlwfcp60KyF9ednFM4i9G_RkzgPCpDXo_NbQbaO_bICxhs_a_mnepi7524BQeK_qdNPRmLt71'),
('paypal.mode','sandbox'),
('url_paypal_cancel','http://localhost/payment/cancel'),
('url_paypal_success','http://localhost/payment/success'),
('servidor.http','http://localhost'),
('app.paypal.return-url','/paypal/order/capture'),
('app.paypal.cancel-url','/paypal/pago_cancel'),
('app.paypal.cancel-error','/paypal/pago_error'),
('app.paypal.cancel-success','/paypal/pago_success');