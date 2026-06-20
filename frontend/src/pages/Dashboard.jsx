import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import API from '../api/api';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalProducts: 0,
    lowStockCount: 0,
    recentUpdates: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [productsRes, lowStockRes, stockRes] = await Promise.all([
        API.get('/products'),
        API.get('/products?lowStock=true'),
        API.get('/stock')
      ]);

      const totalProducts = productsRes.data?.data?.length || 0;
      const lowStockCount = lowStockRes.data?.data?.length || 0;
      const recentUpdates = (stockRes.data?.data || []).slice(0, 5);

      setStats({
        totalProducts,
        lowStockCount,
        recentUpdates
      });
    } catch (err) {
      console.error('Error fetching dashboard stats:', err);
      setError('Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

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
        <h1 className="page-title">&gt;_ INVENTORY_DASHBOARD</h1>
        <p className="page-subtitle">REAL-TIME SYSTEM STATUS AND TELEMETRY</p>
      </div>

      {error && (
        <div className="alert alert-error">
          [CRITICAL ERROR]: {error}
        </div>
      )}

      {/* Stats Cards Grid */}
      <div className="grid-3">
        {/* Total Products */}
        <div className="card total-card">
          <p className="card-label">Total SKU Inventory</p>
          <p className="card-value">{stats.totalProducts}</p>
          <div style={{ marginTop: '15px' }}>
            <Link to="/products" className="nav-link-item" style={{ padding: '4px 0', border: 'none', background: 'none' }}>
              View Catalog &rarr;
            </Link>
          </div>
        </div>

        {/* Low Stock Alerts */}
        <div className="card alert-card">
          <p className="card-label">Low-Stock Alerts</p>
          <p className="card-value" style={{ color: stats.lowStockCount > 0 ? 'var(--danger-color)' : 'var(--text-main)' }}>
            {stats.lowStockCount}
          </p>
          <div style={{ marginTop: '15px' }}>
            {stats.lowStockCount > 0 ? (
              <span className="badge badge-low">Attention Required</span>
            ) : (
              <span className="badge badge-ok">All levels normal</span>
            )}
          </div>
        </div>

        {/* Action Link */}
        <div className="card purple-card">
          <p className="card-label">Active Operations</p>
          <h3 style={{ margin: '15px 0 10px 0', fontWeight: 'bold', fontSize: '1.1rem' }}>Perform Restock</h3>
          <Link to="/stock" className="btn btn-primary btn-sm" style={{ display: 'inline-block', textDecoration: 'none', textAlign: 'center' }}>
            Adjust Stock
          </Link>
        </div>
      </div>

      {/* Recent Updates Log Table */}
      <div className="form-container" style={{ padding: '20px' }}>
        <div className="flex justify-between items-center" style={{ marginBottom: '20px' }}>
          <h2 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: '#fff', fontWeight: 'bold' }}>
            &gt;_ Recent Audit Trail (Stock Updates)
          </h2>
          <Link to="/stock" style={{ color: 'var(--accent-color)', textDecoration: 'none', fontSize: '0.75rem' }}>
            Full Audit History &rarr;
          </Link>
        </div>

        <div className="table-container">
          <table className="console-table">
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>Product Name</th>
                <th>Change Qty</th>
                <th>Updated By</th>
                <th>Notes</th>
              </tr>
            </thead>
            <tbody>
              {stats.recentUpdates.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                    No recent updates recorded
                  </td>
                </tr>
              ) : (
                stats.recentUpdates.map((update) => (
                  <tr key={update.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{new Date(update.updatedAt).toLocaleString()}</td>
                    <td style={{ color: '#fff', fontWeight: '700' }}>{update.product?.name}</td>
                    <td style={{ fontWeight: '700' }}>
                      <span style={{ color: update.changeQty > 0 ? 'var(--accent-color)' : 'var(--danger-color)' }}>
                        {update.changeQty > 0 ? `+${update.changeQty}` : update.changeQty}
                      </span>
                    </td>
                    <td>{update.updatedBy?.fullName}</td>
                    <td style={{ fontStyle: 'italic', color: 'var(--text-muted)' }}>{update.note || '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
