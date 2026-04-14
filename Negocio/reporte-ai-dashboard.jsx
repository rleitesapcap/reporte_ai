import { useState, useEffect, useMemo, useCallback } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, AreaChart, Area, PieChart, Pie, Cell } from "recharts";
import { Search, MapPin, AlertTriangle, CheckCircle, Clock, Users, TrendingUp, ChevronRight, RefreshCw, Filter, Eye, Camera, ArrowUpRight, ArrowDownRight, Minus, ChevronDown, X } from "lucide-react";

// ============================================================================
// API CONFIG — troque para seu Spring Boot real
// ============================================================================
const API_BASE = "http://localhost:8081/api/v1";
const CIDADE_ID = 1;
const REFRESH_INTERVAL = 60000;

// ============================================================================
// MOCK DATA (remover quando conectar ao Spring Boot real)
// ============================================================================
const MOCK = {
  summary: {
    totalOcorrencias: 247, resolvidas: 89, taxaResolucao: 36.0,
    tempoMedio: 4.2, usuariosAtivos: 63, recorrentes: 42,
    abertasSemana: 18, resolvidasSemana: 7,
    porStatus: { RECEBIDA: 38, EM_ANALISE: 24, VALIDADA: 15, EM_RESOLUCAO: 34, RESOLVIDA: 89, ARQUIVADA: 35, REJEITADA: 12 }
  },
  categorias: [
    { nome: "Buracos e Pavimentação", total: 52, trend: 8, cor: "#6B7280" },
    { nome: "Lixo e Resíduos", total: 41, trend: 5, cor: "#EF4444" },
    { nome: "Iluminação Pública", total: 34, trend: 2, cor: "#F59E0B" },
    { nome: "Água e Esgoto", total: 26, trend: -3, cor: "#3B82F6" },
    { nome: "Vegetação", total: 20, trend: 1, cor: "#10B981" },
    { nome: "Trânsito", total: 18, trend: 0, cor: "#8B5CF6" },
    { nome: "Segurança Pública", total: 16, trend: 3, cor: "#EC4899" },
    { nome: "Animais", total: 12, trend: -1, cor: "#F97316" },
    { nome: "Poluição", total: 10, trend: 1, cor: "#6366F1" },
    { nome: "Acessibilidade", total: 8, trend: 0, cor: "#14B8A6" },
  ],
  top5: [
    { protocolo: "RPT-2026-00042", descricao: "Buraco enorme na rua principal, próximo ao posto de gasolina. Já furou pneu de 2 carros.", categoriaNome: "Buracos", scorePrioridade: 18.5, status: "EM_RESOLUCAO", dataRegistro: "2026-04-05", hasImage: true, latitude: -20.6132, longitude: -46.0492 },
    { protocolo: "RPT-2026-00038", descricao: "Esgoto a céu aberto na rua do comércio, mau cheiro insuportável há semanas.", categoriaNome: "Água e Esgoto", scorePrioridade: 17.0, status: "VALIDADA", dataRegistro: "2026-04-03", hasImage: true, latitude: -20.6145, longitude: -46.0478 },
    { protocolo: "RPT-2026-00045", descricao: "Poste apagado há 3 semanas na entrada da cidade, risco de acidente.", categoriaNome: "Iluminação", scorePrioridade: 15.5, status: "EM_ANALISE", dataRegistro: "2026-04-07", hasImage: false, latitude: -20.6118, longitude: -46.0505 },
    { protocolo: "RPT-2026-00029", descricao: "Lixo acumulado no terreno baldio do bairro Canastra, ratos e insetos.", categoriaNome: "Lixo", scorePrioridade: 14.0, status: "EM_RESOLUCAO", dataRegistro: "2026-03-28", hasImage: true, latitude: -20.6170, longitude: -46.0460 },
    { protocolo: "RPT-2026-00041", descricao: "Calçada obstruída por entulho na praça central, pedestres na rua.", categoriaNome: "Acessibilidade", scorePrioridade: 12.5, status: "RECEBIDA", dataRegistro: "2026-04-04", hasImage: true, latitude: -20.6128, longitude: -46.0488 },
  ],
  heatmap: Array.from({ length: 40 }, (_, i) => ({
    lat: -20.61 + (Math.random() - 0.5) * 0.02,
    lng: -46.05 + (Math.random() - 0.5) * 0.02,
    score: Math.round(Math.random() * 20 + 2),
    categoria: ["Buracos", "Lixo", "Iluminação", "Água", "Vegetação"][Math.floor(Math.random() * 5)]
  })),
  timeline: [
    { data: "01/04", ocorrencias: 8, resolvidas: 3 },
    { data: "02/04", ocorrencias: 12, resolvidas: 4 },
    { data: "03/04", ocorrencias: 6, resolvidas: 5 },
    { data: "04/04", ocorrencias: 15, resolvidas: 2 },
    { data: "05/04", ocorrencias: 10, resolvidas: 6 },
    { data: "06/04", ocorrencias: 7, resolvidas: 4 },
    { data: "07/04", ocorrencias: 18, resolvidas: 7 },
    { data: "08/04", ocorrencias: 14, resolvidas: 5 },
    { data: "09/04", ocorrencias: 11, resolvidas: 8 },
  ]
};

// ============================================================================
// API HOOKS (trocar fetch mock por fetch real)
// ============================================================================
function useApi(endpoint, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const refresh = useCallback(() => {
    setLoading(true);
    // MOCK: substituir por fetch real
    setTimeout(() => {
      if (endpoint.includes("summary")) setData(MOCK.summary);
      else if (endpoint.includes("top")) setData(MOCK.top5);
      else if (endpoint.includes("heatmap")) setData(MOCK.heatmap);
      else setData(null);
      setLoading(false);
    }, 300);
    /* REAL:
    fetch(`${API_BASE}${endpoint}`)
      .then(r => r.json())
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false));
    */
  }, [endpoint, ...deps]);
  useEffect(() => { refresh(); const id = setInterval(refresh, REFRESH_INTERVAL); return () => clearInterval(id); }, [refresh]);
  return { data, loading, error, refresh };
}

// ============================================================================
// DESIGN TOKENS
// ============================================================================
const STATUS_CONFIG = {
  RECEBIDA: { label: "Recebida", color: "#6B7280", bg: "#F3F4F6" },
  EM_ANALISE: { label: "Em análise", color: "#3B82F6", bg: "#EFF6FF" },
  VALIDADA: { label: "Validada", color: "#8B5CF6", bg: "#F5F3FF" },
  EM_RESOLUCAO: { label: "Em resolução", color: "#F59E0B", bg: "#FFFBEB" },
  RESOLVIDA: { label: "Resolvida", color: "#10B981", bg: "#ECFDF5" },
  ARQUIVADA: { label: "Arquivada", color: "#9CA3AF", bg: "#F9FAFB" },
  REJEITADA: { label: "Rejeitada", color: "#EF4444", bg: "#FEF2F2" },
};

function scoreColor(s) { return s >= 15 ? "#DC2626" : s >= 10 ? "#D97706" : "#059669"; }
function scoreBg(s) { return s >= 15 ? "#FEF2F2" : s >= 10 ? "#FFFBEB" : "#ECFDF5"; }

// ============================================================================
// COMPONENTS
// ============================================================================

function MetricCard({ icon: Icon, label, value, sub, trend, trendDir }) {
  return (
    <div className="bg-white rounded-xl border border-gray-100 p-4 hover:border-gray-200 transition-colors">
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">{label}</span>
        <Icon size={16} className="text-gray-300" />
      </div>
      <div className="text-2xl font-semibold text-gray-900 tracking-tight">{value}</div>
      {sub && (
        <div className="flex items-center gap-1 mt-1">
          {trendDir === "up" && <ArrowUpRight size={12} className="text-emerald-500" />}
          {trendDir === "down" && <ArrowDownRight size={12} className="text-red-500" />}
          {trendDir === "neutral" && <Minus size={12} className="text-gray-400" />}
          <span className={`text-xs ${trendDir === "up" ? "text-emerald-600" : trendDir === "down" ? "text-red-500" : "text-gray-500"}`}>{sub}</span>
        </div>
      )}
    </div>
  );
}

function CategoryBar({ nome, total, max, trend, cor, rank }) {
  const pct = Math.round((total / max) * 100);
  return (
    <div className="flex items-center gap-3 py-2 group cursor-pointer hover:bg-gray-50 rounded-lg px-2 -mx-2 transition-colors">
      <span className="text-xs font-medium text-gray-400 w-4 text-right">{rank}</span>
      <div className="w-2.5 h-2.5 rounded-full flex-shrink-0" style={{ backgroundColor: cor }} />
      <span className="text-sm text-gray-700 flex-1 truncate">{nome}</span>
      <div className="w-24 h-1.5 bg-gray-100 rounded-full overflow-hidden">
        <div className="h-full rounded-full transition-all duration-500" style={{ width: `${pct}%`, backgroundColor: cor }} />
      </div>
      <span className="text-sm font-semibold text-gray-800 w-8 text-right">{total}</span>
      <span className={`text-xs w-8 text-right font-medium ${trend > 0 ? "text-red-500" : trend < 0 ? "text-emerald-500" : "text-gray-400"}`}>
        {trend > 0 ? `+${trend}` : trend < 0 ? trend : "—"}
      </span>
    </div>
  );
}

function StatusBadge({ status, size = "sm" }) {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.RECEBIDA;
  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full font-medium ${size === "sm" ? "text-xs px-2 py-0.5" : "text-sm px-3 py-1"}`}
      style={{ backgroundColor: cfg.bg, color: cfg.color }}
    >
      <span className="w-1.5 h-1.5 rounded-full" style={{ backgroundColor: cfg.color }} />
      {cfg.label}
    </span>
  );
}

function PriorityRow({ oc, onClick }) {
  return (
    <div onClick={onClick} className="flex items-center gap-3 py-3 border-b border-gray-50 last:border-0 cursor-pointer hover:bg-gray-50 rounded-lg px-2 -mx-2 transition-colors group">
      <div className="flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center text-sm font-bold" style={{ backgroundColor: scoreBg(oc.scorePrioridade), color: scoreColor(oc.scorePrioridade) }}>
        {oc.scorePrioridade.toFixed(1)}
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="text-xs font-mono text-blue-600">{oc.protocolo}</span>
          <StatusBadge status={oc.status} />
          {oc.hasImage && <Camera size={12} className="text-gray-400" />}
        </div>
        <p className="text-sm text-gray-700 truncate mt-0.5">{oc.descricao}</p>
      </div>
      <ChevronRight size={16} className="text-gray-300 group-hover:text-gray-500 transition-colors flex-shrink-0" />
    </div>
  );
}

function HeatmapMock({ points }) {
  const minLat = Math.min(...points.map(p => p.lat));
  const maxLat = Math.max(...points.map(p => p.lat));
  const minLng = Math.min(...points.map(p => p.lng));
  const maxLng = Math.max(...points.map(p => p.lng));
  const toX = lng => 20 + ((lng - minLng) / (maxLng - minLng || 1)) * 360;
  const toY = lat => 20 + ((maxLat - lat) / (maxLat - minLat || 1)) * 220;
  const catColors = { Buracos: "#6B7280", Lixo: "#EF4444", "Iluminação": "#F59E0B", "Água": "#3B82F6", Vegetação: "#10B981" };

  return (
    <div className="bg-gray-50 rounded-xl border border-gray-100 overflow-hidden relative">
      <div className="absolute top-3 left-3 z-10 bg-white/90 backdrop-blur-sm rounded-lg px-3 py-1.5 border border-gray-100">
        <span className="text-xs font-medium text-gray-500">Capitólio - MG</span>
      </div>
      <div className="absolute bottom-3 left-3 z-10 bg-white/90 backdrop-blur-sm rounded-lg px-3 py-2 border border-gray-100">
        <div className="flex items-center gap-3">
          {Object.entries(catColors).map(([k, c]) => (
            <div key={k} className="flex items-center gap-1">
              <div className="w-2 h-2 rounded-full" style={{ backgroundColor: c }} />
              <span className="text-[10px] text-gray-500">{k}</span>
            </div>
          ))}
        </div>
      </div>
      <svg viewBox="0 0 400 260" className="w-full" style={{ minHeight: 260 }}>
        <rect width="400" height="260" fill="#F8FAFC" />
        <path d="M0 200 Q80 185 140 192 Q200 170 260 178 Q320 160 380 170 L400 170 L400 260 L0 260Z" fill="#E2E8F0" opacity="0.3" />
        <path d="M0 220 Q100 210 160 218 Q240 200 300 210 Q360 205 400 208 L400 260 L0 260Z" fill="#CBD5E1" opacity="0.2" />
        {points.map((p, i) => (
          <g key={i}>
            <circle cx={toX(p.lng)} cy={toY(p.lat)} r={Math.max(p.score * 1.2, 6)} fill={catColors[p.categoria] || "#6B7280"} opacity={0.15} />
            <circle cx={toX(p.lng)} cy={toY(p.lat)} r={Math.max(p.score * 0.6, 3)} fill={catColors[p.categoria] || "#6B7280"} opacity={0.3} />
            <circle cx={toX(p.lng)} cy={toY(p.lat)} r={2} fill={catColors[p.categoria] || "#6B7280"} opacity={0.7} />
          </g>
        ))}
        <text x="200" y="252" textAnchor="middle" fontSize="9" fill="#94A3B8" fontFamily="system-ui">Produção: substituir por Leaflet + react-leaflet + leaflet.heat</text>
      </svg>
    </div>
  );
}

function StatusDistribution({ porStatus }) {
  const data = Object.entries(porStatus).map(([k, v]) => ({
    name: STATUS_CONFIG[k]?.label || k,
    value: v,
    color: STATUS_CONFIG[k]?.color || "#6B7280"
  }));
  const total = data.reduce((s, d) => s + d.value, 0);

  return (
    <div className="flex items-center gap-4">
      <div className="w-20 h-20 flex-shrink-0">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie data={data} innerRadius={24} outerRadius={38} paddingAngle={2} dataKey="value" strokeWidth={0}>
              {data.map((d, i) => <Cell key={i} fill={d.color} />)}
            </Pie>
          </PieChart>
        </ResponsiveContainer>
      </div>
      <div className="flex-1 flex flex-wrap gap-x-4 gap-y-1">
        {data.filter(d => d.value > 0).map(d => (
          <div key={d.name} className="flex items-center gap-1.5">
            <div className="w-2 h-2 rounded-full" style={{ backgroundColor: d.color }} />
            <span className="text-xs text-gray-600">{d.name}</span>
            <span className="text-xs font-semibold text-gray-800">{d.value}</span>
            <span className="text-[10px] text-gray-400">({Math.round(d.value / total * 100)}%)</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function TimelineChart({ data }) {
  return (
    <ResponsiveContainer width="100%" height={180}>
      <AreaChart data={data} margin={{ top: 5, right: 5, left: -20, bottom: 0 }}>
        <defs>
          <linearGradient id="gOc" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#3B82F6" stopOpacity={0.15} />
            <stop offset="100%" stopColor="#3B82F6" stopOpacity={0} />
          </linearGradient>
          <linearGradient id="gRes" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#10B981" stopOpacity={0.15} />
            <stop offset="100%" stopColor="#10B981" stopOpacity={0} />
          </linearGradient>
        </defs>
        <XAxis dataKey="data" tick={{ fontSize: 11, fill: "#9CA3AF" }} axisLine={false} tickLine={false} />
        <YAxis tick={{ fontSize: 11, fill: "#9CA3AF" }} axisLine={false} tickLine={false} />
        <Tooltip contentStyle={{ fontSize: 12, borderRadius: 8, border: "1px solid #E5E7EB", boxShadow: "0 4px 12px rgba(0,0,0,0.05)" }} />
        <Area type="monotone" dataKey="ocorrencias" stroke="#3B82F6" fill="url(#gOc)" strokeWidth={2} name="Novas" dot={false} />
        <Area type="monotone" dataKey="resolvidas" stroke="#10B981" fill="url(#gRes)" strokeWidth={2} name="Resolvidas" dot={false} />
      </AreaChart>
    </ResponsiveContainer>
  );
}

function ProtocolModal({ protocolo, onClose }) {
  if (!protocolo) return null;
  const cfg = STATUS_CONFIG[protocolo.status];
  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm z-50 flex items-center justify-center p-4" onClick={onClose}>
      <div className="bg-white rounded-2xl max-w-lg w-full max-h-[80vh] overflow-y-auto shadow-xl" onClick={e => e.stopPropagation()}>
        <div className="p-5 border-b border-gray-100 flex items-center justify-between">
          <div>
            <span className="text-sm font-mono text-blue-600">{protocolo.protocolo}</span>
            <StatusBadge status={protocolo.status} size="md" />
          </div>
          <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded-lg"><X size={18} className="text-gray-400" /></button>
        </div>
        <div className="p-5 space-y-4">
          <div>
            <span className="text-xs text-gray-400 uppercase tracking-wider">Descrição</span>
            <p className="text-sm text-gray-800 mt-1">{protocolo.descricao}</p>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <span className="text-xs text-gray-400 uppercase tracking-wider">Categoria</span>
              <p className="text-sm text-gray-800 mt-1">{protocolo.categoriaNome}</p>
            </div>
            <div>
              <span className="text-xs text-gray-400 uppercase tracking-wider">Score</span>
              <p className="text-sm font-semibold mt-1" style={{ color: scoreColor(protocolo.scorePrioridade) }}>{protocolo.scorePrioridade.toFixed(1)}</p>
            </div>
            <div>
              <span className="text-xs text-gray-400 uppercase tracking-wider">Registrado em</span>
              <p className="text-sm text-gray-800 mt-1">{protocolo.dataRegistro}</p>
            </div>
            <div>
              <span className="text-xs text-gray-400 uppercase tracking-wider">Evidência</span>
              <p className="text-sm text-gray-800 mt-1 flex items-center gap-1">{protocolo.hasImage ? <><Camera size={12} /> Com foto</> : "Sem foto"}</p>
            </div>
          </div>
          {protocolo.latitude && (
            <div>
              <span className="text-xs text-gray-400 uppercase tracking-wider">Localização</span>
              <p className="text-xs font-mono text-gray-500 mt-1">{protocolo.latitude.toFixed(4)}, {protocolo.longitude.toFixed(4)}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// MAIN DASHBOARD
// ============================================================================
export default function Dashboard() {
  const { data: summary, loading: loadingSummary, refresh: refreshSummary } = useApi(`/dashboard/summary?cidadeId=${CIDADE_ID}`);
  const { data: top5, loading: loadingTop } = useApi(`/ocorrencias/top?cidadeId=${CIDADE_ID}&limit=5`);
  const { data: heatmap } = useApi(`/ocorrencias/heatmap?cidadeId=${CIDADE_ID}&dias=30`);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedOc, setSelectedOc] = useState(null);
  const [lastRefresh, setLastRefresh] = useState(new Date());

  const handleRefresh = () => { refreshSummary(); setLastRefresh(new Date()); };

  const searchResult = useMemo(() => {
    if (!searchTerm.match(/^RPT-\d{4}-\d{5}$/i)) return null;
    return MOCK.top5.find(o => o.protocolo.toLowerCase() === searchTerm.toLowerCase()) || { protocolo: searchTerm, notFound: true };
  }, [searchTerm]);

  if (loadingSummary || !summary) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="flex items-center gap-3 text-gray-400">
          <RefreshCw size={20} className="animate-spin" />
          <span className="text-sm">Carregando dashboard...</span>
        </div>
      </div>
    );
  }

  const maxCat = Math.max(...MOCK.categorias.map(c => c.total));

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-100 sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-lg flex items-center justify-center">
              <MapPin size={16} className="text-white" />
            </div>
            <div>
              <h1 className="text-base font-semibold text-gray-900 leading-tight">Reporte AI</h1>
              <p className="text-xs text-gray-400">Capitólio / MG</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="relative">
              <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300" />
              <input
                type="text" placeholder="RPT-2026-00001"
                value={searchTerm} onChange={e => setSearchTerm(e.target.value.toUpperCase())}
                className="pl-8 pr-3 py-1.5 text-xs font-mono bg-gray-50 border border-gray-200 rounded-lg w-44 focus:outline-none focus:border-blue-300 focus:ring-1 focus:ring-blue-100"
              />
              {searchResult && !searchResult.notFound && (
                <div className="absolute top-full mt-1 right-0 bg-white rounded-lg border border-gray-200 shadow-lg p-3 w-72 z-50">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-mono text-blue-600">{searchResult.protocolo}</span>
                    <StatusBadge status={searchResult.status} />
                  </div>
                  <p className="text-xs text-gray-600 line-clamp-2">{searchResult.descricao}</p>
                  <button onClick={() => { setSelectedOc(searchResult); setSearchTerm(""); }} className="mt-2 text-xs text-blue-600 hover:text-blue-700 font-medium flex items-center gap-1">
                    Ver detalhes <Eye size={12} />
                  </button>
                </div>
              )}
              {searchResult?.notFound && (
                <div className="absolute top-full mt-1 right-0 bg-white rounded-lg border border-gray-200 shadow-lg p-3 w-60 z-50">
                  <p className="text-xs text-gray-500">Protocolo não encontrado</p>
                </div>
              )}
            </div>
            <button onClick={handleRefresh} className="p-1.5 hover:bg-gray-100 rounded-lg transition-colors" title="Atualizar">
              <RefreshCw size={14} className="text-gray-400" />
            </button>
            <div className="px-2.5 py-1 bg-emerald-50 text-emerald-700 text-[10px] font-medium rounded-full">
              Dashboard público
            </div>
          </div>
        </div>
      </header>

      {/* Content */}
      <main className="max-w-7xl mx-auto px-4 py-5 space-y-5">
        {/* Metrics */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <MetricCard icon={AlertTriangle} label="Ocorrências" value={summary.totalOcorrencias} sub={`+${summary.abertasSemana} esta semana`} trendDir="up" />
          <MetricCard icon={CheckCircle} label="Resolvidas" value={summary.resolvidas} sub={`${summary.taxaResolucao}% taxa resolução`} trendDir="up" />
          <MetricCard icon={Users} label="Usuários ativos" value={summary.usuariosAtivos} sub={`${summary.recorrentes} recorrentes`} trendDir="neutral" />
          <MetricCard icon={Clock} label="Tempo médio" value={`${summary.tempoMedio}d`} sub="dias para resolver" trendDir="down" />
        </div>

        {/* Main grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
          {/* Left col */}
          <div className="lg:col-span-2 space-y-5">
            {/* Heatmap */}
            <div className="bg-white rounded-xl border border-gray-100 p-4">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-sm font-semibold text-gray-800">Mapa de ocorrências — últimos 30 dias</h2>
                <span className="text-[10px] text-gray-400">{heatmap?.length || 0} pontos</span>
              </div>
              {heatmap && <HeatmapMock points={heatmap} />}
            </div>

            {/* Timeline */}
            <div className="bg-white rounded-xl border border-gray-100 p-4">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-sm font-semibold text-gray-800">Evolução — últimos 9 dias</h2>
                <div className="flex items-center gap-3">
                  <div className="flex items-center gap-1"><div className="w-2 h-2 rounded-full bg-blue-500" /><span className="text-[10px] text-gray-400">Novas</span></div>
                  <div className="flex items-center gap-1"><div className="w-2 h-2 rounded-full bg-emerald-500" /><span className="text-[10px] text-gray-400">Resolvidas</span></div>
                </div>
              </div>
              <TimelineChart data={MOCK.timeline} />
            </div>

            {/* Status distribution */}
            <div className="bg-white rounded-xl border border-gray-100 p-4">
              <h2 className="text-sm font-semibold text-gray-800 mb-3">Distribuição por status</h2>
              <StatusDistribution porStatus={summary.porStatus} />
            </div>
          </div>

          {/* Right col */}
          <div className="space-y-5">
            {/* Categories */}
            <div className="bg-white rounded-xl border border-gray-100 p-4">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-sm font-semibold text-gray-800">Ranking de categorias</h2>
                <span className="text-[10px] text-gray-400">Semana</span>
              </div>
              {MOCK.categorias.map((c, i) => (
                <CategoryBar key={c.nome} nome={c.nome} total={c.total} max={maxCat} trend={c.trend} cor={c.cor} rank={i + 1} />
              ))}
            </div>

            {/* Top 5 */}
            <div className="bg-white rounded-xl border border-gray-100 p-4">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-sm font-semibold text-gray-800">Top 5 prioridade</h2>
                <span className="text-[10px] text-gray-400">Score</span>
              </div>
              {top5 && top5.map(oc => (
                <PriorityRow key={oc.protocolo} oc={oc} onClick={() => setSelectedOc(oc)} />
              ))}
            </div>

            {/* Categories bar chart */}
            <div className="bg-white rounded-xl border border-gray-100 p-4">
              <h2 className="text-sm font-semibold text-gray-800 mb-3">Volume por categoria</h2>
              <ResponsiveContainer width="100%" height={200}>
                <BarChart data={MOCK.categorias.slice(0, 6)} layout="vertical" margin={{ top: 0, right: 10, left: 0, bottom: 0 }}>
                  <XAxis type="number" tick={{ fontSize: 10, fill: "#9CA3AF" }} axisLine={false} tickLine={false} />
                  <YAxis type="category" dataKey="nome" tick={{ fontSize: 10, fill: "#6B7280" }} axisLine={false} tickLine={false} width={90} />
                  <Tooltip contentStyle={{ fontSize: 11, borderRadius: 8, border: "1px solid #E5E7EB" }} />
                  <Bar dataKey="total" radius={[0, 4, 4, 0]} barSize={14}>
                    {MOCK.categorias.slice(0, 6).map((c, i) => <Cell key={i} fill={c.cor} />)}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="text-center py-4 text-[10px] text-gray-300">
          Reporte AI — Inteligência Comunitária • Atualizado {lastRefresh.toLocaleTimeString("pt-BR")} • Dados públicos (LGPD)
        </div>
      </main>

      {/* Modal */}
      <ProtocolModal protocolo={selectedOc} onClose={() => setSelectedOc(null)} />
    </div>
  );
}
