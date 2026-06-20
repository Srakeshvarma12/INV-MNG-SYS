import React from 'react';
import { useAuth } from '../context/AuthContext';

const RoleGuard = ({ requiredRole, children }) => {
  const { user } = useAuth();

  if (!user || user.role !== requiredRole) {
    return (
      <div style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '40px',
        backgroundColor: 'var(--panel-bg)',
        border: '1px solid var(--border-color)',
        borderRadius: '6px',
        maxWidth: '500px',
        margin: '50px auto',
        textAlign: 'center'
      }}>
        <div style={{ fontSize: '32px', marginBottom: '15px' }}>⚠️</div>
        <h2 style={{ color: 'var(--danger-color)', marginBottom: '10px', fontSize: '16px', fontWeight: 'bold' }}>403 FORBIDDEN</h2>
        <p style={{ color: 'var(--text-muted)' }}>
          Access Denied. Operational level insufficient. Required Clearance: [{requiredRole}].
        </p>
      </div>
    );
  }

  return children;
};

export default RoleGuard;
