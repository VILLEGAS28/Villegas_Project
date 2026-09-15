-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-09-2026 a las 17:50:40
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `software_logistic`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `auditoria`
--

CREATE TABLE `auditoria` (
  `id_auditoria` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `modulo` varchar(50) NOT NULL,
  `accion` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias`
--

CREATE TABLE `categorias` (
  `id_categoria` int(11) NOT NULL,
  `nombre` varchar(80) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categorias`
--

INSERT INTO `categorias` (`id_categoria`, `nombre`, `descripcion`, `estado`) VALUES
(1, 'Elementos de protección', 'Elementos utilizados para protección y bioseguridad', 1),
(2, 'Material médico', 'Material utilizado en procedimientos médicos', 1),
(3, 'Medicamentos', 'Productos farmacéuticos y medicamentos', 1),
(4, 'Curaciones', 'Materiales utilizados para curaciones y procedimientos', 1),
(5, 'Equipos e insumos', 'Equipos e insumos generales para operaciones', 1),
(6, 'Dispositivos médicos', 'Dispositivos utilizados para procedimientos médicos', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `id_cliente` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  `ciudad` varchar(80) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`id_cliente`, `nombre`, `telefono`, `correo`, `direccion`, `ciudad`, `estado`) VALUES
(1, 'Cliente Prueba Editado', '3119876543', 'clienteeditado@gmail.com', 'Calle 5 # 10-20', 'Popayan', 1),
(2, 'Hospital Regional', '3002222222', 'compras@hospitalregional.com', 'Calle 15 # 8-30', 'Popayán', 1),
(3, 'Centro Medico del Cauca', '3003333333', 'compras@centromedicocauca.com', 'Carrera 9 # 12-45', 'Popayán', 1),
(4, 'Clinica Corazon y Aorta', '6021234567', 'contacto@corazonyorta.com', 'Carrera 9 # 10-25', 'Popayan', 1),
(5, 'Hospital San Jose', '6027654321', 'compras@hospitalsanjose.com', 'Calle 5 # 8-40', 'Popayan', 1),
(6, 'Distribuciones Medicas del Cauca', '3001234567', 'ventas@dmc.com', 'Carrera 6 # 12-30', 'Popayan', 0),
(9, 'angie lopez', '3106692795', 'angie@hotmail.com', 'calle 11', 'popayan', 1),
(10, 'prueba funcional', '1234556', 'pruebafuncional@softwarelogistic.com', '1', 'popayan', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedido`
--

CREATE TABLE `detalle_pedido` (
  `id_detalle` int(11) NOT NULL,
  `id_pedido` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detalle_pedido`
--

INSERT INTO `detalle_pedido` (`id_detalle`, `id_pedido`, `id_producto`, `cantidad`, `precio_unitario`) VALUES
(2, 1, 4, 20, 18000.00),
(3, 2, 2, 50, 1200.00),
(4, 2, 3, 25, 8500.00),
(5, 3, 5, 10, 9500.00),
(6, 3, 6, 15, 4200.00),
(7, 1, 1, 10, 25000.00),
(8, 1, 1, 10, 25000.00),
(9, 1, 1, 5, 20000.00),
(10, 1, 1, 10, 20000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `entregas`
--

CREATE TABLE `entregas` (
  `id_entrega` int(11) NOT NULL,
  `id_pedido` int(11) NOT NULL,
  `fecha_entrega` datetime DEFAULT NULL,
  `estado` enum('PENDIENTE','EN_RUTA','ENTREGADA','DEVUELTA') NOT NULL DEFAULT 'PENDIENTE',
  `observacion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `entregas`
--

INSERT INTO `entregas` (`id_entrega`, `id_pedido`, `fecha_entrega`, `estado`, `observacion`) VALUES
(1, 1, '2026-09-05 15:30:00', 'PENDIENTE', ''),
(2, 2, NULL, 'EN_RUTA', 'Pedido enviado al cliente'),
(3, 3, '2026-09-09 14:33:00', 'ENTREGADA', 'Entrega  correctamente'),
(11, 9, '2026-09-09 12:34:06', 'ENTREGADA', 'Entregado por juan'),
(12, 14, '2026-09-09 19:06:45', 'ENTREGADA', 'Entregado'),
(13, 15, '2026-09-11 02:37:00', 'EN_RUTA', 'entregada con guia 100230340'),
(14, 16, '2026-09-15 14:04:28', 'ENTREGADA', 'Prueba funcional'),
(15, 17, '2026-09-15 10:24:00', 'ENTREGADA', 'entregada');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inventario`
--

CREATE TABLE `inventario` (
  `id_inventario` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 0,
  `fecha_actualizacion` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `inventario`
--

INSERT INTO `inventario` (`id_inventario`, `id_producto`, `cantidad`, `fecha_actualizacion`) VALUES
(1, 1, 0, '2026-09-09 14:16:40'),
(2, 2, 0, '2026-09-10 21:36:51'),
(3, 3, 150, '2026-08-21 20:39:48'),
(4, 4, 295, '2026-09-15 09:08:00'),
(5, 5, 65, '2026-09-09 14:05:14'),
(6, 6, 100, '2026-08-21 20:39:48'),
(7, 7, 1000, '2026-09-02 23:47:22'),
(8, 8, 117, '2026-09-10 21:36:51'),
(9, 9, 87, '2026-09-15 10:23:44');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `movimientos`
--

CREATE TABLE `movimientos` (
  `id_movimiento` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `tipo` enum('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `stock_anterior` int(11) NOT NULL,
  `stock_nuevo` int(11) NOT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  `fecha_movimiento` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `movimientos`
--

INSERT INTO `movimientos` (`id_movimiento`, `id_producto`, `id_usuario`, `tipo`, `cantidad`, `stock_anterior`, `stock_nuevo`, `observacion`, `fecha_movimiento`) VALUES
(1, 1, 1, 'ENTRADA', 50, 200, 250, 'Ingreso inicial de productos', '2026-08-24 08:24:50'),
(2, 1, 1, 'SALIDA', 20, 250, 230, 'Salida de productos', '2026-08-24 08:25:07'),
(3, 1, 1, 'AJUSTE', 120, 230, 120, 'Ajuste por conteo físico', '2026-08-24 08:25:24'),
(4, 1, 1, 'SALIDA', 100, 120, 20, 'Prueba de salida', '2026-08-24 18:43:21'),
(5, 1, 1, 'ENTRADA', 10, 50, 60, 'Ingreso de mercancía', '2026-09-05 19:49:27'),
(6, 1, 1, 'SALIDA', 5, 60, 55, 'Salida por despacho', '2026-09-05 19:50:11'),
(7, 1, 1, 'AJUSTE', 50, 55, 50, 'Ajuste por conteo físico', '2026-09-05 19:50:53'),
(8, 1, 2, 'SALIDA', 20, 60, 40, 'PRUEBA', '2026-09-09 07:35:15'),
(10, 1, 1, 'ENTRADA', 20, 40, 60, '', '2026-09-09 09:12:41'),
(11, 4, 7, 'SALIDA', 5, 300, 295, 'Salida automática por Pedido #11', '2026-09-09 10:50:38'),
(12, 1, 1, 'SALIDA', 15, 60, 45, 'Salida de Guantes de nitrilo por Pedido #13', '2026-09-09 14:05:14'),
(13, 5, 1, 'SALIDA', 15, 80, 65, 'Salida de Alcohol antiséptico por Pedido #13', '2026-09-09 14:05:14'),
(14, 4, 1, 'SALIDA', 10, 295, 285, 'Salida de Tapabocas quirúrgico por Pedido #14', '2026-09-09 14:05:43'),
(15, 2, 1, 'SALIDA', 10, 10, 0, 'Salida de Jeringa 5 ml por Pedido #15', '2026-09-10 21:36:51'),
(16, 8, 1, 'SALIDA', 3, 120, 117, 'Salida de Algodón hospitalario por Pedido #15', '2026-09-10 21:36:51'),
(17, 4, 1, 'ENTRADA', 10, 285, 295, 'Prueba funcional', '2026-09-15 09:08:00'),
(18, 9, 1, 'SALIDA', 3, 90, 87, 'Salida de Solución salina por Pedido #17', '2026-09-15 10:23:44');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id_pedido` int(11) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `id_cliente` int(11) NOT NULL,
  `estado` enum('PENDIENTE','PROCESANDO','PREPARADO','ENVIADO','ENTREGADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id_pedido`, `fecha`, `id_cliente`, `estado`) VALUES
(1, '2026-08-21 20:40:22', 1, 'PROCESANDO'),
(2, '2026-08-21 20:40:22', 2, 'PENDIENTE'),
(3, '2026-08-21 20:40:22', 3, 'PREPARADO'),
(8, '2026-09-09 07:30:16', 1, 'ENTREGADO'),
(9, '2026-09-09 07:32:20', 2, 'ENVIADO'),
(10, '2026-09-09 07:32:30', 3, 'CANCELADO'),
(11, '2026-09-09 10:50:37', 1, 'ENTREGADO'),
(13, '2026-09-09 14:05:14', 1, 'ENTREGADO'),
(14, '2026-09-09 14:05:43', 6, 'ENVIADO'),
(15, '2026-09-10 21:36:51', 9, 'PREPARADO'),
(16, '2026-09-15 08:57:46', 1, 'PENDIENTE'),
(17, '2026-09-15 10:23:44', 4, 'PROCESANDO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL,
  `codigo` varchar(30) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` decimal(12,2) NOT NULL DEFAULT 0.00,
  `stock_minimo` int(11) NOT NULL DEFAULT 0,
  `id_categoria` int(11) NOT NULL,
  `id_proveedor` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id_producto`, `codigo`, `nombre`, `descripcion`, `precio`, `stock_minimo`, `id_categoria`, `id_proveedor`, `estado`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(1, 'P001', 'Guantes de nitrilo', 'Guantes desechables de nitrilo para procedimientos médicos', 25000.00, 30, 1, 1, 0, '2026-08-21 20:39:19', '2026-09-09 14:32:05'),
(2, 'P002', 'Jeringa 5 ml', 'Jeringa desechable de 5 ml', 1200.00, 30, 2, 2, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(3, 'P003', 'Gasas estériles', 'Paquete de gasas estériles para procedimientos', 8500.00, 15, 4, 3, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(4, 'P004', 'Tapabocas quirúrgico', 'Tapabocas desechable de uso médico', 18000.00, 25, 1, 1, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(5, 'P005', 'Alcohol antiséptico', 'Alcohol antiséptico para limpieza y desinfección', 9500.00, 10, 3, 4, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(6, 'P006', 'Catéter intravenoso', 'Catéter intravenoso para procedimientos médicos', 4200.00, 15, 2, 5, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(7, 'P007', 'Esparadrapo médico', 'Esparadrapo para fijación de material médico', 6500.00, 10, 4, 3, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(8, 'P008', 'Algodón hospitalario', 'Algodón para limpieza y procedimientos', 7200.00, 15, 4, 4, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(9, 'P009', 'Solución salina', 'Solución salina para uso médico', 6800.00, 10, 3, 2, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(10, 'P010', 'Venda elástica', 'Venda elástica para procedimientos médicos', 5500.00, 10, 4, 5, 1, '2026-08-21 20:39:19', '2026-08-21 20:39:19'),
(12, 'p012', 'Glucerna', 'insumo medico', 65000.00, 3, 5, 6, 1, '2026-09-04 23:02:40', '2026-09-04 23:02:59'),
(14, 'P011', 'PEDIASURE', 'PARA NIÑOS', 30000.00, 10, 5, 3, 1, '2026-09-07 20:34:12', '2026-09-07 20:34:12'),
(20, 'P013', 'insulina', 'ninguna', 15000.00, 20, 3, 2, 1, '2026-09-08 12:20:47', '2026-09-08 12:20:47'),
(22, 'P014', 'SUERO FISIOLOGICO', 'Creado por angie', 7000.00, 5, 5, 5, 1, '2026-09-10 21:32:50', '2026-09-10 21:33:53'),
(23, 'P015', 'Prueba funcional', 'Prueba funcional', 10000.00, 10, 1, 1, 1, '2026-09-15 08:19:27', '2026-09-15 08:19:27');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proveedores`
--

CREATE TABLE `proveedores` (
  `id_proveedor` int(11) NOT NULL,
  `nit` varchar(30) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  `ciudad` varchar(80) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `proveedores`
--

INSERT INTO `proveedores` (`id_proveedor`, `nit`, `nombre`, `telefono`, `correo`, `direccion`, `ciudad`, `estado`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(1, '900100001-1', 'MedSupply Colombia', '3001112233', 'ventas@medsupply.com', 'Carrera 10 # 15-20', 'Popayán', 1, '2026-08-21 20:39:06', '2026-08-23 18:41:08'),
(2, '900100002-2', 'Medical Solutions', '3002223344', 'contacto@medicalsolutions.com', 'Calle 5 # 8-30', 'Cali', 1, '2026-08-21 20:39:06', '2026-08-23 18:41:08'),
(3, '900100003-3', 'Distribuciones Salud', '3003334455', 'ventas@distribucionessalud.com', 'Carrera 20 # 10-40', 'Bogotá', 1, '2026-08-21 20:39:06', '2026-08-23 18:41:08'),
(4, '900100004-4', 'Insumos Hospitalarios', '3004445566', 'contacto@insumoshospitalarios.com', 'Calle 12 # 6-25', 'Medellín', 1, '2026-08-21 20:39:06', '2026-08-23 18:41:08'),
(5, '900100005-5', 'TecnoMedical', '3005556677', 'ventas@tecnomedical.com', 'Carrera 30 # 25-10', 'Bogotá', 1, '2026-08-21 20:39:06', '2026-08-23 18:41:08'),
(6, '900123456-7', 'Proveedor Médico S.A.S.', '3001234567', 'ventas@proveedormedico.com', 'Carrera 10 # 15-20', 'Popayán', 1, '2026-08-23 18:42:48', '2026-08-23 18:42:48');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id_rol` int(11) NOT NULL,
  `nombre` varchar(30) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id_rol`, `nombre`, `descripcion`, `estado`) VALUES
(1, 'ADMIN', 'Administrador con acceso completo al sistema', 1),
(2, 'SUPERVISOR', 'Usuario encargado de supervisar el inventario', 1),
(3, 'OPERADOR', 'Usuario encargado de realizar operaciones de inventario', 1),
(8, 'AUXILIAR LOGISTICO', 'Usuario encargado de apoyar las operaciones de logística', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `correo`, `password_hash`, `id_rol`, `estado`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(1, 'Jhonatan', 'Administrador', 'admin@softwarelogistic.com', '$2a$10$BQz.t0G4wSzO0pfVXw3BS.G5TMuCtYFat8oWgFYDOuDAyBbz2VnXm', 1, 1, '2026-08-22 22:14:03', '2026-09-10 10:57:27'),
(2, 'Usuario', 'Prueba', 'usuario.prueba@correo.com', '$2a$10$xbTFzYwj4uIfYpLtCfDE5O8YeQwuxlJEvcUWvMrn8TYZTwwY8.Vn2', 2, 1, '2026-08-22 22:16:16', '2026-09-10 10:56:57'),
(3, 'Andres', 'Operador', 'operador@softwarelogistic.com', '$2a$10$eMTIl0a3L/.e15x8YShoJOwmVGCZqa4mNsKMUt5bCV2L7xA7Y1J0a', 3, 1, '2026-08-22 22:16:43', '2026-09-10 10:57:06'),
(7, 'iriana', 'lopez', 'Irianalopez@softwarelogistic.com', '$2a$10$m2d/laJFMi0QHUCzLNzxk.9OT5jztCw.1U7h3Zkm1cUtrRJw.pjVO', 8, 1, '2026-08-30 21:05:54', '2026-09-10 21:40:00'),
(11, 'brjahma', 'armando', 'brjaham@softwarelogistic.com', '$2a$10$mq1xZ94PELkiytFo4AuqY.aV.jrKdusQZtu7HxnIdw.sDsprCqQOO', 8, 1, '2026-09-10 11:33:09', '2026-09-10 11:33:20'),
(12, 'Prueba', 'funcional', 'pruebafuncional@softwarelogistic.com', '$2a$10$8Jo8xdqiR7Q64aKR7D0oNeVGI4jT/yJCfCHtGHIhqN/5O6CGrDFGK', 3, 1, '2026-09-15 09:09:54', '2026-09-15 09:16:12');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `auditoria`
--
ALTER TABLE `auditoria`
  ADD PRIMARY KEY (`id_auditoria`),
  ADD KEY `fk_auditoria_usuario` (`id_usuario`);

--
-- Indices de la tabla `categorias`
--
ALTER TABLE `categorias`
  ADD PRIMARY KEY (`id_categoria`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id_cliente`);

--
-- Indices de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `fk_detalle_pedido` (`id_pedido`),
  ADD KEY `fk_detalle_producto` (`id_producto`);

--
-- Indices de la tabla `entregas`
--
ALTER TABLE `entregas`
  ADD PRIMARY KEY (`id_entrega`),
  ADD UNIQUE KEY `id_pedido` (`id_pedido`);

--
-- Indices de la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD PRIMARY KEY (`id_inventario`),
  ADD UNIQUE KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `movimientos`
--
ALTER TABLE `movimientos`
  ADD PRIMARY KEY (`id_movimiento`),
  ADD KEY `fk_movimiento_producto` (`id_producto`),
  ADD KEY `fk_movimiento_usuario` (`id_usuario`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `fk_pedido_cliente` (`id_cliente`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id_producto`),
  ADD UNIQUE KEY `codigo` (`codigo`),
  ADD KEY `fk_producto_categoria` (`id_categoria`),
  ADD KEY `fk_producto_proveedor` (`id_proveedor`);

--
-- Indices de la tabla `proveedores`
--
ALTER TABLE `proveedores`
  ADD PRIMARY KEY (`id_proveedor`),
  ADD UNIQUE KEY `nit` (`nit`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `correo` (`correo`),
  ADD KEY `fk_usuario_rol` (`id_rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `auditoria`
--
ALTER TABLE `auditoria`
  MODIFY `id_auditoria` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `categorias`
--
ALTER TABLE `categorias`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  MODIFY `id_detalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `entregas`
--
ALTER TABLE `entregas`
  MODIFY `id_entrega` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `inventario`
--
ALTER TABLE `inventario`
  MODIFY `id_inventario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT de la tabla `movimientos`
--
ALTER TABLE `movimientos`
  MODIFY `id_movimiento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de la tabla `proveedores`
--
ALTER TABLE `proveedores`
  MODIFY `id_proveedor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `auditoria`
--
ALTER TABLE `auditoria`
  ADD CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `fk_detalle_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `entregas`
--
ALTER TABLE `entregas`
  ADD CONSTRAINT `fk_entrega_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD CONSTRAINT `fk_inventario_producto` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `movimientos`
--
ALTER TABLE `movimientos`
  ADD CONSTRAINT `fk_movimiento_producto` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_movimiento_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `fk_pedido_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_producto_proveedor` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
