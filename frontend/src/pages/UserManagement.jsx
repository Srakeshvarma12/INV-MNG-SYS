import React, { useState, useEffect } from 'react';
import API from '../api/api';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form State
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [role, setRole] = useState('STAFF');

  const fetchUsers = async () => {
    try {
      const response = await API.get('/users');
      setUsers(response.data?.data || []);
    } catch (err) {
      console.error('Error fetching users:', err);
      setError('Failed to load user directories');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!username.trim() || !password.trim() || !fullName.trim() || !role) {
      setError('Please fill in all fields');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setSubmitting(true);
    try {
      await API.post('/users', {
        username: username.trim(),
        password: password,
        fullName: fullName.trim(),
        role: role
      });

      setSuccess('User account provisioned successfully');
      
      setUsername('');
      setPassword('');
      setFullName('');
      setRole('STAFF');

      await fetchUsers();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create user account');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="spinner-container">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1 className="page-title">&gt;_ USER_PROVISIONING</h1>
        <p className="page-subtitle">ADMINISTRATIVE CONSOLE DIRECTORY PRIVILEGES</p>
      </div>

      {error && (
        <div className="alert alert-error">
          [ERROR]: {error}
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          [SUCCESS]: {success}
        </div>
      )}

      <div className="grid-form-log">
        {/* Provision Form */}
        <div>
          <div className="form-container">
            <h2 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: '#fff', fontWeight: 'bold', marginBottom: '20px' }}>
              &gt;_ Create Account
            </h2>

            <form onSubmit={handleSubmit}>
              {/* Username */}
              <div className="form-group">
                <label className="form-label">
                  Username <span style={{ color: 'var(--danger-color)' }}>*</span>
                </label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  disabled={submitting}
                  className="input-field"
                  placeholder="e.g. jdoe"
                />
              </div>

              {/* Full Name */}
              <div className="form-group">
                <label className="form-label">
                  Full Name <span style={{ color: 'var(--danger-color)' }}>*</span>
                </label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  disabled={submitting}
                  className="input-field"
                  placeholder="e.g. John Doe"
                />
              </div>

              {/* Password */}
              <div className="form-group">
                <label className="form-label">
                  Password <span style={{ color: 'var(--danger-color)' }}>*</span>
                </label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={submitting}
                  className="input-field"
                  placeholder="Min 6 characters"
                />
              </div>

              {/* Role */}
              <div className="form-group">
                <label className="form-label">
                  Role Assignment <span style={{ color: 'var(--danger-color)' }}>*</span>
                </label>
                <select
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                  disabled={submitting}
                  className="select-field"
                >
                  <option value="STAFF">STAFF</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </div>

              {/* Submit */}
              <button
                type="submit"
                disabled={submitting}
                className="btn btn-primary"
                style={{ width: '100%', padding: '10px', marginTop: '10px' }}
              >
                {submitting ? 'COMMITTING...' : 'PROVISION USER'}
              </button>
            </form>
          </div>
        </div>

        {/* Directory List */}
        <div>
          <div className="form-container" style={{ padding: '20px' }}>
            <h2 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: '#fff', fontWeight: 'bold', marginBottom: '20px' }}>
              &gt;_ System Users Registry
            </h2>

            <div className="table-container">
              <table className="console-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Created At</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id}>
                      <td style={{ color: 'var(--text-muted)' }}>{u.id}</td>
                      <td style={{ color: '#fff', fontWeight: '700' }}>{u.username}</td>
                      <td style={{ color: 'var(--text-main)' }}>{u.fullName}</td>
                      <td>
                        <span className={`badge ${u.role === 'ADMIN' ? 'badge-admin' : 'badge-staff'}`}>
                          {u.role}
                        </span>
                      </td>
                      <td style={{ color: 'var(--text-muted)' }}>
                        {u.createdAt ? new Date(u.createdAt).toLocaleString() : '-'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;
