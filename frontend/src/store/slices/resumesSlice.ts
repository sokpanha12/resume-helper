import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import type { Resume } from '../../types';
import { getResumes, uploadResume, deleteResume } from '../../api/resumes';

interface ResumesState {
  items: Resume[];
  loading: boolean;
  error: string;
}

const initialState: ResumesState = { items: [], loading: false, error: '' };

export const fetchResumes  = createAsyncThunk('resumes/fetch',  async (_, { rejectWithValue }) => {
  try { return await getResumes(); }
  catch (e: any) { return rejectWithValue(e.response?.data?.message || 'Failed to load resumes.'); }
});
export const uploadFile    = createAsyncThunk('resumes/upload', async (file: File, { rejectWithValue }) => {
  try { return await uploadResume(file); }
  catch (e: any) { return rejectWithValue(e.response?.data?.message || 'Upload failed.'); }
});
export const removeResume  = createAsyncThunk('resumes/delete', async (id: number, { rejectWithValue }) => {
  try { await deleteResume(id); return id; }
  catch (e: any) { return rejectWithValue(e.response?.data?.message || 'Delete failed.'); }
});

const resumesSlice = createSlice({
  name: 'resumes',
  initialState,
  reducers: {},
  extraReducers: builder => {
    builder
      .addCase(fetchResumes.pending,    s => { s.loading = true; s.error = ''; })
      .addCase(fetchResumes.fulfilled,  (s, a) => { s.loading = false; s.items = a.payload; })
      .addCase(fetchResumes.rejected,   (s, a) => { s.loading = false; s.error = a.payload as string; })
      .addCase(uploadFile.fulfilled,    (s, a) => { s.items.push(a.payload); })
      .addCase(uploadFile.rejected,     (s, a) => { s.error = a.payload as string; })
      .addCase(removeResume.fulfilled,  (s, a) => { s.items = s.items.filter(r => r.id !== a.payload); })
      .addCase(removeResume.rejected,   (s, a) => { s.error = a.payload as string; });
  },
});

export default resumesSlice.reducer;
