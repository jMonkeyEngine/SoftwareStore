-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Generation Time: Oct 22, 2020 at 09:16 PM
-- Server version: 10.5.6-MariaDB-1:10.5.6+maria~focal
-- PHP Version: 7.4.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `jmestore`
--

-- --------------------------------------------------------

--
-- Table structure for table `badges`
--

CREATE TABLE `badges` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) NOT NULL,
  `icon` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `badges_users`
--

CREATE TABLE `badges_users` (
  `badge_id` bigint(20) NOT NULL,
  `users_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `parent_id` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `images`
--

CREATE TABLE `images` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `live_pages`
--

CREATE TABLE `live_pages` (
  `id` varchar(64) NOT NULL,
  `hosted_dependencies` varchar(1000) DEFAULT NULL,
  `repositories` varchar(1000) DEFAULT NULL,
  `store_dependencies` varchar(650) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_updated` datetime NOT NULL,
  `description` varchar(10000) NOT NULL,
  `short_description` varchar(255) NOT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `title` varchar(64) NOT NULL,
  `docs_website` varchar(255) DEFAULT NULL,
  `publisher_website` varchar(255) DEFAULT NULL,
  `image_ids` varchar(1000) DEFAULT NULL,
  `video_ids` varchar(1000) DEFAULT NULL,
  `fork` bit(1) NOT NULL,
  `fork_repository` varchar(1024) DEFAULT NULL,
  `git_repository` varchar(1024) DEFAULT NULL,
  `media_license` varchar(255) DEFAULT NULL,
  `software_license` varchar(255) DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `purchase_count` bigint(20) NOT NULL,
  `software_type` varchar(255) DEFAULT NULL,
  `engine_compatibility` varchar(255) DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `version` varchar(128) NOT NULL,
  `average_rating` float NOT NULL,
  `five_star_count` int(11) NOT NULL,
  `four_star_count` int(11) NOT NULL,
  `one_star_count` int(11) NOT NULL,
  `rating_count` int(11) NOT NULL,
  `three_star_count` int(11) NOT NULL,
  `two_star_count` int(11) NOT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `background_image_index` int(11) DEFAULT -1,
  `hub_link` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` bigint(20) NOT NULL,
  `date` bigint(20) NOT NULL,
  `delivered` bit(1) NOT NULL,
  `message` varchar(10000) DEFAULT NULL,
  `title` varchar(128) DEFAULT NULL,
  `recipient_id` bigint(20) DEFAULT NULL,
  `sender_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `message_replies`
--

CREATE TABLE `message_replies` (
  `id` bigint(20) NOT NULL,
  `content` varchar(10000) DEFAULT NULL,
  `date` bigint(20) NOT NULL,
  `message_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `page_amendments`
--

CREATE TABLE `page_amendments` (
  `id` varchar(64) NOT NULL,
  `hosted_dependencies` varchar(1000) DEFAULT NULL,
  `repositories` varchar(1000) DEFAULT NULL,
  `store_dependencies` varchar(650) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_updated` datetime NOT NULL,
  `description` varchar(10000) NOT NULL,
  `short_description` varchar(255) NOT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `title` varchar(64) NOT NULL,
  `docs_website` varchar(255) DEFAULT NULL,
  `publisher_website` varchar(255) DEFAULT NULL,
  `image_ids` varchar(1000) DEFAULT NULL,
  `video_ids` varchar(1000) DEFAULT NULL,
  `fork` bit(1) NOT NULL,
  `fork_repository` varchar(1024) DEFAULT NULL,
  `git_repository` varchar(1024) DEFAULT NULL,
  `media_license` varchar(255) DEFAULT NULL,
  `software_license` varchar(255) DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `purchase_count` bigint(20) NOT NULL,
  `software_type` varchar(255) DEFAULT NULL,
  `engine_compatibility` varchar(255) DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `version` varchar(128) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `parent_page_id` varchar(64) NOT NULL,
  `review_state` varchar(255) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `reviewer_id` bigint(20) DEFAULT NULL,
  `background_image_index` int(11) DEFAULT -1,
  `hub_link` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `page_drafts`
--

CREATE TABLE `page_drafts` (
  `id` varchar(64) NOT NULL,
  `hosted_dependencies` varchar(1000) DEFAULT NULL,
  `repositories` varchar(1000) DEFAULT NULL,
  `store_dependencies` varchar(650) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_updated` datetime NOT NULL,
  `description` varchar(10000) NOT NULL,
  `short_description` varchar(255) NOT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `title` varchar(64) NOT NULL,
  `docs_website` varchar(255) DEFAULT NULL,
  `publisher_website` varchar(255) DEFAULT NULL,
  `image_ids` varchar(1000) DEFAULT NULL,
  `video_ids` varchar(1000) DEFAULT NULL,
  `fork` bit(1) NOT NULL,
  `fork_repository` varchar(1024) DEFAULT NULL,
  `git_repository` varchar(1024) DEFAULT NULL,
  `media_license` varchar(255) DEFAULT NULL,
  `software_license` varchar(255) DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `purchase_count` bigint(20) NOT NULL,
  `software_type` varchar(255) DEFAULT NULL,
  `engine_compatibility` varchar(255) DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `version` varchar(128) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `review_state` varchar(255) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `reviewer_id` bigint(20) DEFAULT NULL,
  `background_image_index` int(11) DEFAULT -1,
  `hub_link` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `id` bigint(20) NOT NULL,
  `content` varchar(1000) NOT NULL,
  `date_created` datetime NOT NULL,
  `date_updated` datetime NOT NULL,
  `page_id` varchar(64) DEFAULT NULL,
  `rating` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `sessions`
--

CREATE TABLE `sessions` (
  `id` bigint(20) NOT NULL,
  `ip_address` varchar(32) NOT NULL,
  `session` varchar(32) NOT NULL,
  `agent` varchar(128) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `staff_reviews`
--

CREATE TABLE `staff_reviews` (
  `id` bigint(20) NOT NULL,
  `date_reviewed` datetime NOT NULL,
  `page_id` varchar(64) NOT NULL,
  `page_state` varchar(255) DEFAULT NULL,
  `review` varchar(10000) DEFAULT NULL,
  `reviewer_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `admin` bit(1) NOT NULL,
  `avatar_id` varchar(256) DEFAULT NULL,
  `email` varchar(256) NOT NULL,
  `moderator` bit(1) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `password` varchar(257) NOT NULL,
  `register_date` datetime NOT NULL,
  `username` varchar(64) NOT NULL,
  `trust_level` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users_badges`
--

CREATE TABLE `users_badges` (
  `user_id` bigint(20) NOT NULL,
  `badges_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_validation`
--

CREATE TABLE `user_validation` (
  `id` varchar(64) NOT NULL,
  `creation_date` datetime NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `validation_type` varchar(255) DEFAULT NULL,
  `value` varchar(257) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `badges`
--
ALTER TABLE `badges`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `badges_users`
--
ALTER TABLE `badges_users`
  ADD KEY `FKjudmf7j4xy8i7mqbr5ufoeawe` (`users_id`),
  ADD KEY `FKcnq6blxoyoqhls503xlihk7at` (`badge_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id`);

--
-- Indexes for table `images`
--
ALTER TABLE `images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK13ljqfrfwbyvnsdhihwta8cpr` (`user_id`);

--
-- Indexes for table `live_pages`
--
ALTER TABLE `live_pages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKsvdryy1vyro9jjh2d6nlsenk1` (`owner_id`),
  ADD KEY `FK2l3s76me44sdr7atg8phw4lpy` (`category_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKhdkwfnspwb3s60j27vpg0rpg6` (`recipient_id`),
  ADD KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`);

--
-- Indexes for table `message_replies`
--
ALTER TABLE `message_replies`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK7mn9vstuy94mr88w9m2lv0ws1` (`message_id`),
  ADD KEY `FKl0ndtu1kmg1x48n48dmu0hb1k` (`user_id`);

--
-- Indexes for table `page_amendments`
--
ALTER TABLE `page_amendments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKqm19m2ahtd2wbagoiwbg4fqws` (`owner_id`),
  ADD KEY `FK9175t6ghla6o0e0kat51ma650` (`reviewer_id`);

--
-- Indexes for table `page_drafts`
--
ALTER TABLE `page_drafts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKbkkobhf27ah9f105u09oq5wea` (`owner_id`),
  ADD KEY `FK3ab0ukt6qlxetsiiq252jb2rl` (`reviewer_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`);

--
-- Indexes for table `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_n0evcxh8yhoxq0genomg8k5u3` (`session`),
  ADD KEY `FKruie73rneumyyd1bgo6qw8vjt` (`user_id`);

--
-- Indexes for table `staff_reviews`
--
ALTER TABLE `staff_reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK8nkyxk8r4ej47ra31woruewr` (`reviewer_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UK_r53o2ojjw4fikudfnsuuga336` (`password`),
  ADD UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  ADD UNIQUE KEY `UK_rsulcn2gynjy3cddpwmosv881` (`avatar_id`);

--
-- Indexes for table `users_badges`
--
ALTER TABLE `users_badges`
  ADD KEY `FKs3hfrtpdc3pchgngowqvmald9` (`badges_id`),
  ADD KEY `FKjwn8va6yevsjelsktidp2mdvm` (`user_id`);

--
-- Indexes for table `user_validation`
--
ALTER TABLE `user_validation`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `badges`
--
ALTER TABLE `badges`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `message_replies`
--
ALTER TABLE `message_replies`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sessions`
--
ALTER TABLE `sessions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `staff_reviews`
--
ALTER TABLE `staff_reviews`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
