import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { complaintService } from '../services/complaintService';
import './ComplaintDetails.css';

const ComplaintDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [complaint, setComplaint] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [updatingStatus, setUpdatingStatus] = useState(false);

  useEffect(() => {
    fetchComplaint();
  }, [id]);

  const fetchComplaint = async () => {
    try {
      setLoading(true);
      const data = await complaintService.getComplaintById(id);
      setComplaint(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch complaint details. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (e) => {
    const newStatus = e.target.value;
    if (!newStatus || newStatus === complaint.status) return;

    try {
      setUpdatingStatus(true);
      const updated = await complaintService.updateComplaintStatus(id, newStatus);
      setComplaint(updated);
    } catch (err) {
      alert('Failed to update status. Please try again.');
      console.error(err);
    } finally {
      setUpdatingStatus(false);
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'OPEN':
        return 'status-open';
      case 'IN_PROGRESS':
        return 'status-in-progress';
      case 'RESOLVED':
        return 'status-resolved';
      default:
        return '';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return <div className="loading">Loading complaint details...</div>;
  }

  if (error) {
    return (
      <div className="error">
        {error}
        <button onClick={() => navigate('/')} className="btn-primary" style={{ marginTop: '20px' }}>
          Back to Dashboard
        </button>
      </div>
    );
  }

  if (!complaint) {
    return <div className="error">Complaint not found</div>;
  }

  return (
    <div className="complaint-details">
      <div className="card">
        <div className="details-header">
          <h1>Complaint Details</h1>
          <button onClick={() => navigate('/')} className="btn-secondary">
            Back to Dashboard
          </button>
        </div>

        <div className="details-content">
          <div className="detail-row">
            <span className="detail-label">ID:</span>
            <span className="detail-value">{complaint.id}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Title:</span>
            <span className="detail-value">{complaint.title}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Description:</span>
            <span className="detail-value">{complaint.description}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Category:</span>
            <span className="detail-value">{complaint.category}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Status:</span>
            <span className={`status-badge ${getStatusClass(complaint.status)}`}>
              {complaint.status.replace('_', ' ')}
            </span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Assigned To:</span>
            <span className="detail-value">{complaint.assignedTo}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Raised By:</span>
            <span className="detail-value">
              {complaint.raisedBy?.name} ({complaint.raisedBy?.role})
            </span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Created At:</span>
            <span className="detail-value">{formatDate(complaint.createdAt)}</span>
          </div>

          {complaint.attachmentUrl && (
            <div className="detail-row">
              <span className="detail-label">Attachment:</span>
              <a
                href={`http://localhost:8080${complaint.attachmentUrl}`}
                target="_blank"
                rel="noopener noreferrer"
                className="attachment-link"
              >
                View Attachment
              </a>
            </div>
          )}

          <div className="detail-row">
            <span className="detail-label">Update Status:</span>
            <select
              value={complaint.status}
              onChange={handleStatusUpdate}
              disabled={updatingStatus}
              className="status-select"
            >
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ComplaintDetails;
