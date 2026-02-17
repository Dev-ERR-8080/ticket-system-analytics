import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          Hostel Complaint System
        </Link>
        <ul className="navbar-menu">
          <li>
            <Link to="/" className="navbar-link">
              Dashboard
            </Link>
          </li>
          <li>
            <Link to="/create" className="navbar-link">
              Create Complaint
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
