-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : mysql:3306
-- Généré le : mer. 08 jan. 2025 à 12:16
-- Version du serveur : 8.0.40
-- Version de PHP : 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `h2online`
--

-- --------------------------------------------------------

--
-- Structure de la table `brands`
--

CREATE TABLE `brands` (
  `brand_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `brands`
--

INSERT INTO `brands` (`brand_id`, `name`, `description`) VALUES
(1, 'Evian', 'Evian is a brand of mineral water from the French Alps, known for its purity.'),
(2, 'Perrier', 'Perrier is a famous brand of sparkling water from France, known for its refreshing taste.'),
(3, 'Volvic', 'Volvic offers natural and flavored still waters, sourced from the volcanic region of France.'),
(4, 'Nestlé Pure Life', 'Nestlé Pure Life is a popular brand of purified bottled water.'),
(5, 'Fiji', 'Natural artesian water from the islands of Fiji.'),
(6, 'Smartwater', 'Vapor-distilled water with added electrolytes for taste.'),
(7, 'San Pellegrino', 'Sparkling natural mineral water from Italy.');

-- --------------------------------------------------------

--
-- Structure de la table `categories`
--

CREATE TABLE `categories` (
  `category_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `categories`
--

INSERT INTO `categories` (`category_id`, `name`, `description`) VALUES
(1, 'Still', 'Non-carbonated water.'),
(2, 'Sparkling', 'Carbonated water.'),
(3, 'Flavored', 'Water with added flavors.'),
(4, '6-Pack', 'Pack of 6 bottles.'),
(5, 'Large', 'Bottles with a capacity greater than 1 liter.'),
(6, 'Small', 'Bottles with a capacity of 500ml or less.'),
(7, 'Mineral', 'Water rich in natural minerals.'),
(8, 'Spring', 'Water sourced from natural springs.');

-- --------------------------------------------------------

--
-- Structure de la table `invoices`
--

CREATE TABLE `invoices` (
  `invoice_id` int NOT NULL,
  `order_id` int NOT NULL,
  `invoice_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(10,2) NOT NULL,
  `payment_status` enum('paid','unpaid') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `invoices`
--

INSERT INTO `invoices` (`invoice_id`, `order_id`, `invoice_date`, `total_amount`, `payment_status`) VALUES
(1, 1, '2024-12-13 13:22:36', 15.50, 'paid'),
(2, 2, '2024-12-13 13:22:36', 9.60, 'unpaid'),
(3, 4, '2024-12-24 17:44:34', 39.50, 'paid'),
(4, 5, '2024-12-24 18:04:39', 1.80, 'paid'),
(5, 6, '2024-12-24 18:30:56', 34.00, 'paid'),
(6, 7, '2024-12-25 20:04:27', 19.30, 'paid'),
(7, 8, '2024-12-25 23:54:11', 10.70, 'paid');

-- --------------------------------------------------------

--
-- Structure de la table `orderitems`
--

CREATE TABLE `orderitems` (
  `order_item_id` int NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `subtotal_price` decimal(10,2) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `orderitems`
--

INSERT INTO `orderitems` (`order_item_id`, `order_id`, `product_id`, `quantity`, `unit_price`, `subtotal_price`) VALUES
(9, 4, 15, 1, 3.00, 3.00),
(8, 4, 25, 1, 11.50, 11.50),
(10, 4, 4, 1, 8.50, 8.50),
(16, 5, 2, 1, 1.80, 1.80),
(12, 4, 17, 1, 1.30, 1.30),
(13, 4, 23, 1, 10.00, 10.00),
(14, 4, 22, 1, 3.20, 3.20),
(15, 4, 19, 1, 2.00, 2.00),
(17, 6, 4, 4, 8.50, 34.00),
(18, 7, 2, 6, 1.80, 10.80),
(19, 7, 4, 1, 8.50, 8.50),
(20, 8, 2, 4, 1.80, 7.20),
(21, 8, 7, 1, 3.50, 3.50);

-- --------------------------------------------------------

--
-- Structure de la table `orders`
--

CREATE TABLE `orders` (
  `order_id` int NOT NULL,
  `user_id` int NOT NULL,
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` enum('in progress','validated','delivered') NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `payment_method` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `order_date`, `status`, `total_price`, `payment_method`) VALUES
(4, 5, '2024-12-23 22:31:33', 'delivered', 42.30, NULL),
(5, 5, '2024-12-24 17:44:55', 'delivered', 1.80, NULL),
(6, 5, '2024-12-24 18:30:49', 'validated', 34.00, NULL),
(7, 5, '2024-12-25 20:04:04', 'validated', 19.30, NULL),
(8, 18, '2024-12-25 23:54:00', 'validated', 10.70, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `products`
--

CREATE TABLE `products` (
  `product_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `volume_per_bottle` decimal(10,2) DEFAULT NULL,
  `description` text,
  `image` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `products`
--

INSERT INTO `products` (`product_id`, `name`, `volume_per_bottle`, `description`, `image`, `price`, `stock_quantity`, `created_at`, `updated_at`) VALUES
(1, 'Evian Still Water', 1.50, 'Pure still water from the Alps.', 'evian_1_5l.png', 1.40, 100, '2024-12-15 17:41:37', '2024-12-25 21:10:18'),
(2, 'Perrier Sparkling Water', 0.75, 'Refreshing sparkling water.', 'perrier_0_75l.png', 1.80, 200, '2024-12-15 17:41:37', '2024-12-18 11:18:25'),
(3, 'Volvic Lemon Water', 0.50, 'Still water with a lemon flavor.', 'volvic_citron_0_5l.jpg', 1.20, 150, '2024-12-15 17:41:37', '2024-12-18 11:18:25'),
(4, '6-Pack of Evian', 1.50, 'Pack of 6 Evian still water bottles.', 'evian_6_pack_1_5l.jpg', 8.50, 50, '2024-12-15 17:41:37', '2024-12-18 11:18:25'),
(5, '6-Pack of Perrier', 1.00, 'Pack of 6 Perrier sparkling water bottles.', 'perrier_6_pack_1l.jpg', 10.50, 0, '2024-12-15 17:41:37', '2024-12-23 11:10:18'),
(6, 'Fiji Natural Water', 0.50, 'Smooth-tasting water from Fiji.', 'fiji_500ml.png', 2.00, 150, '2024-12-23 13:54:26', '2024-12-24 16:15:24'),
(7, 'Fiji Natural Water', 1.00, 'Smooth-tasting water from Fiji.', 'fiji_1l.png', 3.50, 100, '2024-12-23 13:54:26', '2024-12-24 16:15:29'),
(8, 'Smartwater Distilled', 0.50, 'Distilled water with electrolytes.', 'smartwater_500ml.png', 1.80, 200, '2024-12-23 13:54:26', '2024-12-24 16:15:33'),
(9, 'Smartwater Distilled', 1.50, 'Distilled water with electrolytes.', 'smartwater_1_5l.png', 2.80, 120, '2024-12-23 13:54:26', '2024-12-24 16:15:40'),
(10, 'San Pellegrino Sparkling', 0.75, 'Italian sparkling mineral water.', 'sanpellegrino_750ml.png', 2.50, 180, '2024-12-23 13:54:26', '2024-12-24 16:15:45'),
(11, 'San Pellegrino Sparkling', 1.00, 'Italian sparkling mineral water.', 'sanpellegrino_1l.png', 3.00, 100, '2024-12-23 13:54:26', '2024-12-24 16:15:50'),
(12, 'Evian Kids', 0.33, 'Small bottle of still water for kids.', 'evian_kids_330ml.png', 1.00, 300, '2024-12-23 13:54:26', '2024-12-24 16:15:55'),
(13, 'Evian Sports', 0.75, 'Easy-grip bottle for sports.', 'evian_sports_750ml.png', 1.50, 200, '2024-12-23 13:54:26', '2024-12-24 16:16:00'),
(14, 'Perrier Lime', 0.50, 'Sparkling water with lime flavor.', 'perrier_lime_500ml.png', 1.90, 150, '2024-12-23 13:54:26', '2024-12-24 16:16:05'),
(15, 'Perrier Lime', 1.00, 'Sparkling water with lime flavor.', 'perrier_lime_1l.png', 3.00, 80, '2024-12-23 13:54:26', '2024-12-24 16:16:10'),
(16, 'Volvic Strawberry', 1.00, 'Still water with strawberry flavor.', 'volvic_strawberry_1l.png', 2.20, 150, '2024-12-23 13:54:26', '2024-12-24 16:16:15'),
(17, 'Volvic Orange', 0.50, 'Still water with orange flavor.', 'volvic_orange_500ml.png', 1.30, 150, '2024-12-23 13:54:26', '2024-12-24 16:16:19'),
(18, 'Nestlé Pure Life Kids', 0.33, 'Small bottle of purified water for kids.', 'nestle_kids_330ml.png', 1.10, 250, '2024-12-23 13:54:26', '2024-12-24 16:16:23'),
(19, 'Nestlé Pure Life Large ', 2.00, 'Large bottle of purified water.', 'nestle_large_2l.png', 2.00, 120, '2024-12-23 13:54:26', '2024-12-24 16:16:28'),
(20, 'San Pellegrino Lemon', 0.50, 'Sparkling water with lemon flavor.', 'sanpellegrino_lemon_500ml.png', 2.00, 100, '2024-12-23 13:54:26', '2024-12-24 16:16:33'),
(21, 'San Pellegrino Orange', 0.50, 'Sparkling water with orange flavor.', 'sanpellegrino_orange_500ml.png', 2.00, 100, '2024-12-23 13:54:26', '2024-12-24 16:16:38'),
(22, 'Smartwater Alkaline', 1.00, 'Distilled water with alkaline properties.', 'smartwater_alkaline_1l.png', 3.20, 70, '2024-12-23 13:54:26', '2024-12-24 16:16:43'),
(23, 'Fiji Large Pack', 1.50, 'Pack of 6 large Fiji bottles.', 'fiji_large_pack_1_5l.png', 10.00, 50, '2024-12-23 13:54:26', '2024-12-24 16:16:48'),
(24, 'Evian 6-Pack Kids', 0.33, 'Pack of 6 small Evian bottles.', 'evian_6pack_kids.png', 6.00, 80, '2024-12-23 13:54:26', '2024-12-23 13:54:26'),
(25, 'Perrier 6-Pack Lime', 0.50, 'Pack of 6 Perrier Lime bottles.', 'perrier_6pack_lime.png', 11.50, 40, '2024-12-23 13:54:26', '2024-12-23 13:54:26');

-- --------------------------------------------------------

--
-- Structure de la table `productsbrands`
--

CREATE TABLE `productsbrands` (
  `product_id` int NOT NULL,
  `brand_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `productsbrands`
--

INSERT INTO `productsbrands` (`product_id`, `brand_id`) VALUES
(1, 1),
(4, 1),
(12, 1),
(13, 1),
(24, 1),
(2, 2),
(5, 2),
(14, 2),
(15, 2),
(25, 2),
(3, 3),
(16, 3),
(17, 3),
(18, 4),
(19, 4),
(6, 5),
(7, 5),
(23, 5),
(8, 6),
(9, 6),
(22, 6),
(10, 7),
(11, 7),
(20, 7),
(21, 7);

-- --------------------------------------------------------

--
-- Structure de la table `productscategories`
--

CREATE TABLE `productscategories` (
  `category_id` int NOT NULL,
  `product_id` int NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `productscategories`
--

INSERT INTO `productscategories` (`category_id`, `product_id`) VALUES
(1, 1),
(1, 4),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 12),
(1, 13),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 22),
(1, 23),
(1, 24),
(2, 2),
(2, 5),
(2, 10),
(2, 11),
(2, 14),
(2, 15),
(2, 20),
(2, 21),
(2, 25),
(3, 3),
(3, 14),
(3, 15),
(3, 16),
(3, 17),
(3, 20),
(3, 21),
(3, 25),
(4, 4),
(4, 5),
(4, 23),
(4, 24),
(4, 25),
(5, 1),
(5, 7),
(5, 9),
(5, 15),
(5, 16),
(5, 19),
(5, 22),
(6, 2),
(6, 3),
(6, 6),
(6, 8),
(6, 12),
(6, 13),
(6, 17),
(6, 18),
(7, 10),
(7, 11),
(7, 20),
(7, 21);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `postal_code` varchar(10) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('customer','admin') DEFAULT 'customer',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `phone_number`, `address`, `city`, `postal_code`, `country`, `password`, `role`, `created_at`, `updated_at`) VALUES
(5, 'Max', 'Villame', 'max.vil@gmail.com', '0649456547', '15 Rue Ducis', 'Versailles', '78000', 'France', '863a7376103ff66a0ba5af8dc04ee2e2e93c47bd09444c133bcb10ff91c3926e', 'admin', '2024-12-13 16:05:50', '2024-12-25 20:06:42'),
(18, 'mathis', 'smet', 'mathis.smet@gmail.com', '0645546547', '15 rue ducis', 'Paris', '75001', 'France', '4a44dc15364204a80fe80e9039455cc1608281820fe2b24f1e5233ade6af1dd5', 'customer', '2024-12-25 23:43:36', '2024-12-25 23:53:45');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `brands`
--
ALTER TABLE `brands`
  ADD PRIMARY KEY (`brand_id`);

--
-- Index pour la table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Index pour la table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`invoice_id`),
  ADD KEY `order_id` (`order_id`);

--
-- Index pour la table `orderitems`
--
ALTER TABLE `orderitems`
  ADD PRIMARY KEY (`order_item_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Index pour la table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`);

--
-- Index pour la table `productsbrands`
--
ALTER TABLE `productsbrands`
  ADD PRIMARY KEY (`product_id`,`brand_id`),
  ADD KEY `brand_id` (`brand_id`);

--
-- Index pour la table `productscategories`
--
ALTER TABLE `productscategories`
  ADD PRIMARY KEY (`category_id`,`product_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `brands`
--
ALTER TABLE `brands`
  MODIFY `brand_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `invoice_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `orderitems`
--
ALTER TABLE `orderitems`
  MODIFY `order_item_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT pour la table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `productsbrands`
--
ALTER TABLE `productsbrands`
  ADD CONSTRAINT `productsbrands_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `productsbrands_ibfk_2` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`brand_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
