-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-12-2025 a las 04:19:29
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
-- Base de datos: `huahuacuna`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `apadrinamientos`
--

CREATE TABLE `apadrinamientos` (
  `id_apadrinamiento` int(11) NOT NULL,
  `id_padrino` int(11) NOT NULL,
  `id_nino` int(11) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  `estado` enum('ACTIVO','FINALIZADO','PENDIENTE') DEFAULT 'ACTIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `apadrinamientos`
--

INSERT INTO `apadrinamientos` (`id_apadrinamiento`, `id_padrino`, `id_nino`, `fecha_inicio`, `fecha_fin`, `estado`) VALUES
(1, 6, 4, '2025-11-30', NULL, 'ACTIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bitacora`
--

CREATE TABLE `bitacora` (
  `id_bitacora` int(11) NOT NULL,
  `id_apadrinamiento` int(11) NOT NULL,
  `fecha_registro` date NOT NULL DEFAULT curdate(),
  `descripcion` varchar(255) DEFAULT NULL,
  `foto_url` varchar(255) DEFAULT NULL,
  `video_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `bitacora`
--

INSERT INTO `bitacora` (`id_bitacora`, `id_apadrinamiento`, `fecha_registro`, `descripcion`, `foto_url`, `video_url`) VALUES
(1, 1, '2025-11-30', 'la niña participó en actividades de lectura', '', '');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `contenido`
--

CREATE TABLE `contenido` (
  `id_contenido` int(11) NOT NULL,
  `tipo` enum('imagen','video','documento') NOT NULL,
  `url` varchar(255) NOT NULL,
  `titulo` varchar(100) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_subida` date NOT NULL,
  `seccion` enum('proyecto','evento','fundacion','otro') DEFAULT 'otro'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `donaciones`
--

CREATE TABLE `donaciones` (
  `id_donacion` bigint(20) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `tipo` enum('monetaria','material') NOT NULL,
  `monto` decimal(38,2) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_donacion` datetime(6) DEFAULT NULL,
  `estado` enum('pendiente','completada','cancelada') DEFAULT 'pendiente',
  `banco` varchar(255) DEFAULT NULL,
  `correo_electronico` varchar(255) DEFAULT NULL,
  `nit` varchar(255) DEFAULT NULL,
  `tipo_dotacion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `eventos`
--

CREATE TABLE `eventos` (
  `id` bigint(20) NOT NULL,
  `activo` bit(1) DEFAULT NULL,
  `descripcion` text NOT NULL,
  `descripcion_detallada` text DEFAULT NULL,
  `fecha_actualizacion` datetime(6) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_evento` datetime(6) DEFAULT NULL,
  `horario` varchar(255) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `lugar` varchar(255) DEFAULT NULL,
  `titulo` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `eventos`
--

INSERT INTO `eventos` (`id`, `activo`, `descripcion`, `descripcion_detallada`, `fecha_actualizacion`, `fecha_creacion`, `fecha_evento`, `horario`, `imagen_url`, `lugar`, `titulo`) VALUES
(1, b'1', 'Un evento para promover el arte local y la cultura regional.', 'Plaza Central, Huahuacuna', '2025-11-13 11:35:18.000000', '2025-11-13 11:35:18.000000', '2024-11-13 10:30:00.000000', '2 a 4 ', NULL, 'armenia', 'Festival Cultural 2025'),
(3, b'1', 'Un evento para promover el deporte y la cultura regional.', 'Plaza Central, cordoba', '2025-11-13 11:40:57.000000', '2025-11-13 11:37:01.000000', '2026-03-12 10:30:00.000000', '2 a 6 ', NULL, 'cordoba', 'Festival de deportes 2026'),
(5, b'1', 'Un evento para promover el arte local y la cultura regional.', 'Plaza Central, cordoba', '2025-11-13 11:38:38.000000', '2025-11-13 11:38:38.000000', '2026-01-12 10:30:00.000000', '10 a 6 ', NULL, 'cordoba', 'Festival Cultural 2026');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inscripcion_evento`
--

CREATE TABLE `inscripcion_evento` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `estado` enum('CANCELADO','CONFIRMADO') DEFAULT NULL,
  `fecha_inscripcion` datetime(6) DEFAULT NULL,
  `nombre_completo` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `evento_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ninos`
--

CREATE TABLE `ninos` (
  `id_nino` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `genero` varchar(255) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `foto_url` varchar(255) DEFAULT NULL,
  `estado_apadrinamiento` enum('Disponible','Apadrinado','Inactivo') DEFAULT 'Disponible',
  `fecha_registro` date DEFAULT curdate(),
  `fecha_nacimiento` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ninos`
--

INSERT INTO `ninos` (`id_nino`, `nombre`, `genero`, `descripcion`, `foto_url`, `estado_apadrinamiento`, `fecha_registro`, `fecha_nacimiento`) VALUES
(1, 'pepito', 'masculino', 'Niño timido y responsable', 'aaaa', 'Apadrinado', '2025-11-02', '2016-02-08'),
(3, 'pepe', 'masculino', 'Niño estrovertido y alegre', 'eeee', 'Disponible', '2025-11-02', '2017-11-01'),
(4, 'catalina', 'FEMENINO', 'niña feliz', NULL, 'Disponible', '2025-11-30', '2018-08-24'),
(5, 'pablito', 'MASCULINO', 'niño introvertido', NULL, 'Disponible', '2025-11-30', '2019-12-01'),
(6, 'pablito', 'MASCULINO', 'niño introvertido', NULL, 'Disponible', '2025-11-30', '2019-12-01');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `noticias`
--

CREATE TABLE `noticias` (
  `id_noticia` int(11) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `contenido` text NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `fecha_publicacion` date NOT NULL,
  `estado` enum('publicado','borrador') DEFAULT 'publicado'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyectos`
--

CREATE TABLE `proyectos` (
  `id_proyecto` int(11) NOT NULL,
  `nombre_proyecto` varchar(255) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','FINALIZADO') DEFAULT 'ACTIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `proyectos`
--

INSERT INTO `proyectos` (`id_proyecto`, `nombre_proyecto`, `descripcion`, `fecha_inicio`, `fecha_fin`, `estado`) VALUES
(2, 'clases de musica', 'Proyecto para enseñar a los niños a tocar instrumentos', '2025-04-24', '2025-12-14', 'INACTIVO'),
(3, 'clases de futbol', 'Proyecto para enseñar a los niños a jugar futbol', '2025-02-02', '2025-09-09', 'FINALIZADO'),
(4, 'Huerto Escolar', 'Proyecto para enseñar a los niños sobre agricultura sostenible.', '2025-10-01', '2026-03-01', 'ACTIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `rol` enum('administrador','voluntario','padrino') NOT NULL,
  `fecha_creacion` date NOT NULL,
  `estado` enum('activo','inactivo') NOT NULL,
  `recovery_code` varchar(255) DEFAULT NULL,
  `recovery_expiry` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `correo`, `contrasena`, `telefono`, `direccion`, `rol`, `fecha_creacion`, `estado`, `recovery_code`, `recovery_expiry`) VALUES
(1, 'Juan Pérez', 'juan@gmail.com', '12345', '3001234567', 'Calle 10 #5-20', 'padrino', '2025-10-13', 'activo', NULL, NULL),
(2, 'Juan Perez', 'juan.perez@example.com', '12345678', '3001234567', 'Calle 123', 'voluntario', '2025-10-18', 'activo', NULL, NULL),
(3, 'Juan Diego', 't4mara95@gmail.com', 'tamara26', '3001234567', 'Calle 123', 'administrador', '2025-10-18', 'activo', NULL, NULL),
(4, 'Juanito perez', 'juanperez@example.com', '56789', '30000004', 'Centro', 'voluntario', '2025-10-19', 'activo', NULL, NULL),
(5, 'jose', 'jose123@gmail.com', 'jose20', NULL, NULL, 'voluntario', '2025-11-02', 'activo', NULL, NULL),
(6, 'Juan Pérez', 'juan@example.com', '12345678', NULL, NULL, 'padrino', '2025-11-30', 'activo', NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `voluntariado`
--

CREATE TABLE `voluntariado` (
  `id_voluntariado` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_proyecto` int(11) NOT NULL,
  `rol_voluntario` varchar(255) DEFAULT NULL,
  `fecha_inscripcion` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `voluntariado`
--

INSERT INTO `voluntariado` (`id_voluntariado`, `id_usuario`, `id_proyecto`, `rol_voluntario`, `fecha_inscripcion`) VALUES
(1, 5, 4, 'Colaborador', '2025-11-02');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `apadrinamientos`
--
ALTER TABLE `apadrinamientos`
  ADD PRIMARY KEY (`id_apadrinamiento`),
  ADD UNIQUE KEY `id_padrino` (`id_padrino`),
  ADD KEY `id_nino` (`id_nino`);

--
-- Indices de la tabla `bitacora`
--
ALTER TABLE `bitacora`
  ADD PRIMARY KEY (`id_bitacora`),
  ADD KEY `fk_bitacora_apadrinamiento` (`id_apadrinamiento`);

--
-- Indices de la tabla `contenido`
--
ALTER TABLE `contenido`
  ADD PRIMARY KEY (`id_contenido`);

--
-- Indices de la tabla `donaciones`
--
ALTER TABLE `donaciones`
  ADD PRIMARY KEY (`id_donacion`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `eventos`
--
ALTER TABLE `eventos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `inscripcion_evento`
--
ALTER TABLE `inscripcion_evento`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK71elhj0i579brg8x57pfjjovv` (`evento_id`);

--
-- Indices de la tabla `ninos`
--
ALTER TABLE `ninos`
  ADD PRIMARY KEY (`id_nino`);

--
-- Indices de la tabla `noticias`
--
ALTER TABLE `noticias`
  ADD PRIMARY KEY (`id_noticia`);

--
-- Indices de la tabla `proyectos`
--
ALTER TABLE `proyectos`
  ADD PRIMARY KEY (`id_proyecto`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`);

--
-- Indices de la tabla `voluntariado`
--
ALTER TABLE `voluntariado`
  ADD PRIMARY KEY (`id_voluntariado`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_proyecto` (`id_proyecto`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `apadrinamientos`
--
ALTER TABLE `apadrinamientos`
  MODIFY `id_apadrinamiento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `bitacora`
--
ALTER TABLE `bitacora`
  MODIFY `id_bitacora` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `contenido`
--
ALTER TABLE `contenido`
  MODIFY `id_contenido` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `donaciones`
--
ALTER TABLE `donaciones`
  MODIFY `id_donacion` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `eventos`
--
ALTER TABLE `eventos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `inscripcion_evento`
--
ALTER TABLE `inscripcion_evento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ninos`
--
ALTER TABLE `ninos`
  MODIFY `id_nino` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `noticias`
--
ALTER TABLE `noticias`
  MODIFY `id_noticia` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `proyectos`
--
ALTER TABLE `proyectos`
  MODIFY `id_proyecto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `voluntariado`
--
ALTER TABLE `voluntariado`
  MODIFY `id_voluntariado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `apadrinamientos`
--
ALTER TABLE `apadrinamientos`
  ADD CONSTRAINT `apadrinamientos_ibfk_1` FOREIGN KEY (`id_padrino`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `apadrinamientos_ibfk_2` FOREIGN KEY (`id_nino`) REFERENCES `ninos` (`id_nino`);

--
-- Filtros para la tabla `bitacora`
--
ALTER TABLE `bitacora`
  ADD CONSTRAINT `fk_bitacora_apadrinamiento` FOREIGN KEY (`id_apadrinamiento`) REFERENCES `apadrinamientos` (`id_apadrinamiento`);

--
-- Filtros para la tabla `donaciones`
--
ALTER TABLE `donaciones`
  ADD CONSTRAINT `donaciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`);

--
-- Filtros para la tabla `inscripcion_evento`
--
ALTER TABLE `inscripcion_evento`
  ADD CONSTRAINT `FK71elhj0i579brg8x57pfjjovv` FOREIGN KEY (`evento_id`) REFERENCES `eventos` (`id`);

--
-- Filtros para la tabla `voluntariado`
--
ALTER TABLE `voluntariado`
  ADD CONSTRAINT `voluntariado_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `voluntariado_ibfk_2` FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos` (`id_proyecto`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
