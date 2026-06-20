import React, { useState, useEffect } from 'react';
import API from '../api/api';

const StockUpdate = () => {
  const [products, setProducts] = useState([]);
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form State
  const [selectedProductId, setSelectedProductId] = useState('');
  const [changeQty, setChangeQty] = useState('');
  const [note, setNote] = useState('');

  const fetchData = async () => {
    try {
      const [productsRes, logsRes] = await Promise.all([
        API.get('/products'),
        API.get('/stock')
      ]);
      setProducts(productsRes.data?.data || []);
      setLogs(logsRes.data?.data || []);
    } catch (err) {
      console.error('Error fetching stock page data:', err);
      setError('Failed to load stock telemetry and audit logs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!selectedProductId) {
      setError('Please select a product');
      return;
    }

    const qtyVal = parseInt(changeQty);
    if (isNaN(qtyVal) || qtyVal === 0) {
      setError('Please specify a non-zero quantity change (positive to add stock, negative to remove)');
      return;
    }

    setSubmitting(true);
    try {
      const response = await API.post('/stock', {
        productId: parseInt(selectedProductId),
        changeQty: qtyVal,
        note: note.trim() || null
      });

      setSuccess(response.data?.message || 'Stock level adjusted successfully');
      
      // Reset Form
      setSelectedProductId('');
      setChangeQty('');
      setNote('');

      // Refresh Data
      await fetchData();
      
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to submit stock adjustment');
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
        <h1 className="page-title">&gt;_ STOCK_OPERATIONS</h1>
        <p className="page-subtitle">LOG STOCK DISCREPANCIES AND RESTOCK INVENTORY</p>
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
        {/* Form Column */}
        <div>
          <div className="form-container">
            <h2 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: '#fff', fontWeight: 'bold', marginBottom: '20px' }}>
              &gt;_ Log Adjustment
            </h2>

            <form onSubmit={handleSubmit}>
              {/* Product Select */}
              <div className="form-group">
                <label className="form-label">
                  Product SKU <span style={{ color: 'var(--danger-color)' }}>*</span>
                </label>
                <select
                  value={selectedProductId}
                  onChange={(e) => setSelectedProductId(e.target.value)}
                  disabled={submitting}
                  className="select-field"
                >
                  <option value="">CHOOSE SKU RECORD</option>
                  {products.map(p => (
                    <option key={p.id} value={p.id}>
                      {p.sku} | {p.name} (Current: {p.quantity})
                    </option>
                  ))}
                </select>
              </div>

              {/* Quantity Change */}
              <div className="form-group">
                <label className="form-label">
                  Quantity Adjustment <span style={{ color: 'var(--danger-color)' }}>*</span>
                </label>
                <input
                  type="number"
                  value={changeQty}
                  onChange={(e) => setChangeQty(e.target.value)}
                  disabled={submitting}
                  className="input-field"
                  placeholder="e.g. +10 (Add) or -3 (Remove)"
                />
                <p style={{ fontSize: '0.65rem', color: 'var(--text-muted)', marginTop: '5px' }}>
                  Use positive numbers to add stock, negative to subtract.
                </p>
              </div>

              {/* Note */}
              <div className="form-group">
                <label className="form-label">Operation Note</label>
                <textarea
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  disabled={submitting}
                  rows="3"
                  className="textarea-field"
                  placeholder="Explain reason for adjustment..."
                />
              </div>

              {/* Submit */}
              <button
                type="submit"
                disabled={submitting}
                className="btn btn-primary"
                style={{ width: '100%', padding: '10px', marginTop: '10px' }}
              >
                {submitting ? 'TRANSMITTING...' : 'COMMIT TRANSACTION'}
              </button>
            </form>
          </div>
        </div>

        {/* Audit Log Table Column */}
        <div>
          <div className="form-container" style={{ padding: '20px' }}>
            <h2 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: '#fff', fontWeight: 'bold', marginBottom: '20px' }}>
              &gt;_ Audit Log Registry
            </h2>

            <div className="table-container">
              <table className="console-table">
                <thead>
                  <tr>
                    <th>Timestamp</th>
                    <th>Product Name</th>
                    <th style={{ textAlign: 'center' }}>Delta</th>
                    <th>Operator</th>
                    <th>Transaction Note</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.length === 0 ? (
                    <tr>
                      <td colSpan="5" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '20px' }}>
                        No transactions registered in registry log
                      </td>
                    </tr>
                  ) : (
                    logs.map((log) => (
                      <tr key={log.id}>
                        <td style={{ color: 'var(--text-muted)' }}>{new Date(log.updatedAt).toLocaleString()}</td>
                        <td style={{ color: '#fff', fontWeight: '700' }}>{log.product?.name}</td>
                        <td style={{ textAlign: 'center', fontWeight: '700' }}>
                          <span style={{ color: log.changeQty > 0 ? 'var(--accent-color)' : 'var(--danger-color)' }}>
                            {log.changeQty > 0 ? `+${log.changeQty}` : log.changeQty}
                          </span>
                        </td>
                        <td>{log.updatedBy?.fullName}</td>
                        <td style={{ fontStyle: 'italic', color: 'var(--text-muted)' }}>{log.note || '-'}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StockUpdate;
