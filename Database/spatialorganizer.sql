-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 14, 2018 at 09:50 AM
-- Server version: 10.1.21-MariaDB
-- PHP Version: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `spatialorganizer`
--

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE `employee` (
  `employee_id` int(11) NOT NULL,
  `name` varchar(127) NOT NULL,
  `password` varchar(127) NOT NULL,
  `location_id` int(11) NOT NULL,
  `approval_status` int(11) NOT NULL DEFAULT '1' COMMENT '0: Not approved, 1: approved',
  `newTaskFlag` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`employee_id`, `name`, `password`, `location_id`, `approval_status`, `newTaskFlag`) VALUES
(1, 'Saiful', '827ccb0eea8a706c4c34a16891f84e7b', 62, 1, 0),
(2, 'Shemonti', '827ccb0eea8a706c4c34a16891f84e7b', 65, 1, 0),
(3, 'Moury', '827ccb0eea8a706c4c34a16891f84e7b', 64, 1, 0),
(4, 'Azad', '827ccb0eea8a706c4c34a16891f84e7b', 65, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
  `location_id` int(11) NOT NULL,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `location`
--

INSERT INTO `location` (`location_id`, `lat`, `lon`, `description`) VALUES
(60, 23.7256, 90.3925, 'BUET'),
(61, 23.7256, 90.3925, NULL),
(62, 23.7256, 90.3935, NULL),
(63, 23.72797030618947, 90.3925047442317, NULL),
(64, 23.719510010298645, 90.38797952234744, NULL),
(65, 23.7256, 90.3925, NULL),
(66, 23.7256, 90.3935, NULL),
(67, 23.7256, 90.3925, NULL),
(68, 23.7256, 90.3935, NULL),
(69, 23.72797030618947, 90.3925047442317, NULL),
(70, 23.72726128675999, 90.39664976298809, NULL),
(71, 23.72797030618947, 90.3925047442317, NULL),
(72, 23.732948066476258, 90.38449566811323, NULL),
(73, 23.72581530488047, 90.3905886411667, NULL),
(74, 23.72336437346911, 90.39618473500013, NULL),
(75, 23.7256, 90.3935, NULL),
(76, 23.720910633427604, 90.39864800870419, NULL),
(77, 23.7256, 90.3935, NULL),
(78, 23.7256, 90.39450000000001, NULL),
(79, 23.719588897427382, 90.38802813738585, NULL),
(80, 23.72625729637374, 90.41190147399902, NULL),
(81, 23.7360834, 90.363067, NULL),
(82, 23.735604799999997, 90.4236016, NULL),
(83, 23.72797030618947, 90.3925047442317, NULL),
(84, 23.72937789783629, 90.412525087595, NULL),
(85, 23.7464653, 90.3760125, NULL),
(86, 23.810332, 90.4125181, NULL),
(87, 23.7256, 90.3935, NULL),
(88, 23.717634201517598, 90.40210135281087, NULL),
(89, 23.7256, 90.3925, NULL),
(90, 23.7256, 90.3935, NULL),
(91, 23.8186416, 90.4129073, NULL),
(92, 23.7464653, 90.3760125, NULL),
(93, 23.7957902, 90.40082129999999, NULL),
(94, 23.810332, 90.4125181, NULL),
(95, 23.72797030618947, 90.3925047442317, NULL),
(96, 23.722820159153777, 90.37412390112877, NULL),
(97, 23.7464653, 90.3760125, NULL),
(98, 23.7658444, 90.3583606, NULL),
(99, 23.7368013, 90.3837176, NULL),
(100, 23.782062399999997, 90.4160527, NULL),
(101, 23.7427161, 90.3883735, NULL),
(102, 23.7957902, 90.40082129999999, NULL),
(103, 23.710610199999998, 90.43490919999999, NULL),
(104, 23.7217917, 90.4260691, NULL),
(105, 23.7957902, 90.40082129999999, NULL),
(106, 23.810332, 90.4125181, NULL),
(107, 23.749423099999998, 90.3830754, NULL),
(108, 23.7464653, 90.3760125, NULL),
(109, 23.7270488, 90.388999, NULL),
(110, 23.718175799999997, 90.3866074, NULL),
(111, 23.7805462, 90.4266584, NULL),
(112, 23.782062399999997, 90.4160527, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `system_admin`
--

CREATE TABLE `system_admin` (
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `system_admin`
--

INSERT INTO `system_admin` (`username`, `password`) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3');

-- --------------------------------------------------------

--
-- Table structure for table `task`
--

CREATE TABLE `task` (
  `task_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `employee_id` int(11) DEFAULT NULL,
  `description` varchar(511) NOT NULL,
  `date` date NOT NULL,
  `duration_mins` double NOT NULL,
  `delivery_start_time` time DEFAULT NULL,
  `delivery_deadline` time DEFAULT NULL,
  `pickup_location_id` int(11) NOT NULL,
  `delivery_location_id` int(11) NOT NULL,
  `assignment_status` int(11) NOT NULL COMMENT '0: unassigned, 1: pending, 2: done and dusted :)'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `task`
--

INSERT INTO `task` (`task_id`, `user_id`, `employee_id`, `description`, `date`, `duration_mins`, `delivery_start_time`, `delivery_deadline`, `pickup_location_id`, `delivery_location_id`, `assignment_status`) VALUES
(16, 5, 1, 'task1', '2018-02-14', 10, '11:45:00', '12:45:00', 62, 61, 1),
(17, 5, NULL, 'task2', '2018-03-11', 0, '11:46:00', '11:47:00', 64, 63, 1),
(18, 5, 1, 'task3', '2018-02-11', 0, '16:42:00', '16:42:00', 66, 65, 1),
(19, 5, 2, 'asas', '2018-02-11', 1, '17:14:00', '17:15:00', 68, 67, 1),
(20, 6, 1, 'morning', '2018-02-12', 10, '17:58:00', '19:12:00', 70, 69, 1),
(21, 6, NULL, 'evening', '2018-03-11', 22, '20:00:00', '21:05:00', 72, 71, 1),
(22, 7, 4, 'good morning', '2018-02-12', 10, '18:01:00', '19:02:00', 74, 73, 1),
(23, 7, 2, 'good night', '2018-02-12', 5, '18:03:00', '18:04:00', 76, 75, 1),
(24, 7, 4, 'hello', '2018-02-11', 6, '18:04:00', '18:04:00', 78, 77, 1),
(25, 5, 2, 'afternoon', '2018-02-13', 15, '14:34:00', '15:34:00', 80, 79, 1),
(26, 5, 4, 'college', '2018-02-13', 11, '15:35:00', '15:38:00', 82, 81, 1),
(27, 6, 1, 'mosque', '2018-02-13', 5, '14:37:00', '14:45:00', 84, 83, 1),
(28, 6, 2, 'dress', '2018-02-13', 50, '15:38:00', '18:38:00', 86, 85, 1),
(29, 7, 3, 'hello', '2018-02-13', 10, '14:40:00', '15:40:00', 88, 87, 1),
(30, 5, 1, 'no task', '2018-02-13', 12, '15:04:00', '15:05:00', 90, 89, 1),
(31, 5, 3, 'golf', '2018-02-13', 15, '15:05:00', '18:05:00', 92, 91, 1),
(32, 5, 4, 'banani', '2018-02-13', 14, '16:40:00', '17:40:00', 94, 93, 1),
(33, 6, 3, 'golap gram', '2018-02-14', 30, '11:25:00', '14:23:00', 96, 95, 1),
(34, 7, 2, 'wedding party', '2018-02-14', 30, '18:40:00', '20:00:00', 98, 97, 1),
(35, 7, 4, 'north end', '2018-02-14', 10, '19:40:00', '20:40:00', 100, 99, 2),
(36, 7, 3, 'home delivery', '2018-02-14', 20, '17:00:00', '21:00:00', 102, 101, 1),
(37, 6, 3, 'jatra', '2018-02-14', 20, '18:12:00', '20:12:00', 104, 103, 1),
(38, 5, 3, 'shopping', '2018-02-14', 30, '18:17:00', '18:25:00', 106, 105, 1),
(39, 5, 4, 'bus ticket', '2018-02-14', 20, '12:20:00', '21:20:00', 108, 107, 1),
(40, 6, 1, 'buet', '2018-02-14', 5, '17:21:00', '19:21:00', 110, 109, 1),
(41, 7, 3, 'dingi', '2018-02-14', 50, '17:23:00', '20:23:00', 112, 111, 1);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `home_location_id` int(11) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `home_location_id`, `email`, `password`) VALUES
(5, 60, 'a@t', '827ccb0eea8a706c4c34a16891f84e7b'),
(6, 63, 'test1@gmail.com', '827ccb0eea8a706c4c34a16891f84e7b'),
(7, 66, 'test2@gmail.com', '827ccb0eea8a706c4c34a16891f84e7b');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `employee`
--
ALTER TABLE `employee`
  ADD PRIMARY KEY (`employee_id`),
  ADD KEY `loc_foreign` (`location_id`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`location_id`);

--
-- Indexes for table `task`
--
ALTER TABLE `task`
  ADD PRIMARY KEY (`task_id`),
  ADD KEY `user_fk` (`user_id`),
  ADD KEY `emp_fk` (`employee_id`),
  ADD KEY `pickup_loc_fk` (`pickup_location_id`),
  ADD KEY `delivery_loc_fk` (`delivery_location_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `home_loc_fk` (`home_location_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `employee`
--
ALTER TABLE `employee`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
  MODIFY `location_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=113;
--
-- AUTO_INCREMENT for table `task`
--
ALTER TABLE `task`
  MODIFY `task_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `loc_foreign` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`);

--
-- Constraints for table `task`
--
ALTER TABLE `task`
  ADD CONSTRAINT `delivery_loc_fk` FOREIGN KEY (`delivery_location_id`) REFERENCES `location` (`location_id`),
  ADD CONSTRAINT `emp_fk` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`),
  ADD CONSTRAINT `pickup_loc_fk` FOREIGN KEY (`pickup_location_id`) REFERENCES `location` (`location_id`),
  ADD CONSTRAINT `user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `home_loc_fk` FOREIGN KEY (`home_location_id`) REFERENCES `location` (`location_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
