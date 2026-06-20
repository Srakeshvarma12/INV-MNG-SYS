import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const navItemClass = ({ isActive }) =>
    `nav-link-item ${isActive ? 'active' : ''}`;

  return (
    <nav className="navbar">
      <div className="flex items-center gap-8">
        {/* Brand/Logo */}
        <div className="nav-brand">
          &gt;_ INV_MNG_SYS
        </div>

        {/* Navigation Links */}
        <div className="nav-links">
          <NavLink to="/" className={navItemClass}>
            Dashboard
          </NavLink>
          <NavLink to="/products" className={navItemClass} end>
            Products
          </NavLink>
          <NavLink to="/stock" className={navItemClass}>
            Stock Logs
          </NavLink>
          {user.role === 'ADMIN' && (
            <NavLink to="/users" className={navItemClass}>
              Users
            </NavLink>
          )}
        </div>
      </div>

      {/* User Session Info */}
      <div className="nav-profile">
        <div className="nav-user-info">
          <p className="nav-username">{user.fullName}</p>
          <span className={`badge ${user.role === 'ADMIN' ? 'badge-admin' : 'badge-staff'}`}>
            {user.role}
          </span>
        </div>
        <button
          onClick={handleLogout}
          className="btn btn-sm"
          style={{ padding: '6px 12px' }}
        >
          LOGOUT
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
