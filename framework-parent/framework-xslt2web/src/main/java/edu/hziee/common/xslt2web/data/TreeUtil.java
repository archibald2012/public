package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;

import edu.hziee.common.xslt2web.exception.ErrorPageException;
import edu.hziee.common.xslt2web.provider.GlobalProvider;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public final class TreeUtil {
	private TreeUtil() {
	}

	private static String addLayer(String value) {
		int nextValue = Integer.parseInt(value) + 1;
		return StringUtil.padLeft(Integer.toString(nextValue), 3, '0');
	}

	public static void setAddedRow(AbstractXmlTableResolver resolver,
			DataRow row, TreeFieldGroup fields) {
		row.setItem(fields.getIsLeaf(), 1);
		row.setItem(fields.getLayer(), getLayer(resolver, fields, row.getItem(
				fields.getParentID()).toString()));
	}

	public static IParamBuilder getIdParamBuilder(TreeFieldGroup fields,
			String fieldName, String value) {
		IParamBuilder result = SQLParamBuilder.getEqualSQL(fields.getIdType(),
				fieldName, value);
		return result;
	}

	public static IParamBuilder getLayerParamBuilder(TreeFieldGroup fields,
			String layerValue) {
		IParamBuilder result = SQLParamBuilder.getCompositeSQL(
				"%s != %s AND %s LIKE %s", new TypeCode[] {
						TypeCode.String, TypeCode.String }, new String[] {
						fields.getLayer(), fields.getLayer() }, layerValue,
				layerValue + "%");
		return result;
	}

	private static void setParentLeaf(AbstractXmlTableResolver resolver,
			TreeFieldGroup fields, String parentID) {
		DataTable table = resolver.getHostTable();

		IParamBuilder parentBuilder = getIdParamBuilder(fields, fields
				.getParentID(), parentID);
		String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s",
				resolver.getTableName(), parentBuilder.getSQL());
		int count = Integer.parseInt(DataSetUtil.executeScalar(
				resolver.getConnection(), sql, parentBuilder.getParams())
				.toString());
		if (count == 1) {
			int currentCount = table.getRows().size();
			resolver.selectWithParam(fields.getId(), parentID);
			if (table.getRows().size() > currentCount) {
				DataRow row = table.getRows().getItem(currentCount);
				row.setItem(fields.getIsLeaf(), 1);
			}
		}
	}

	public static String getLayer(AbstractXmlTableResolver resolver,
			TreeFieldGroup fields, String parentID) {
		if ("-1".equals(parentID) || "".equals(parentID)) {
			String subStringSql = GlobalProvider.getSqlProvider().getFunction(
					"SubString", fields.getLayer(), 1, 3);
			String sql = String.format("SELECT MAX(%s) FROM %s", subStringSql,
					resolver.getTableName());
			String value = DataSetUtil.executeScalar(resolver.getConnection(),
					sql).toString();
			if ("".equals(value))
				return "000";
			else
				return addLayer(value);
		} else {
			try {
				IParamBuilder idBuilder = getIdParamBuilder(fields, fields
						.getId(), parentID);
				String sql = String.format("SELECT %s FROM %s WHERE %s",
						fields.getLayer(), resolver.getTableName(), idBuilder
								.getSQL());
				String topLayer = DataSetUtil.executeScalar(
						resolver.getConnection(), sql, idBuilder.getParams())
						.toString();
				String subStringSql = GlobalProvider.getSqlProvider()
						.getFunction("SubString", fields.getLayer(),
								topLayer.length() + 1, 3);

				IParamBuilder layerBuilder = getLayerParamBuilder(fields,
						topLayer);
				sql = String.format("SELECT MAX(%s) FROM %s WHERE %s",
						subStringSql, resolver.getTableName(), layerBuilder
								.getSQL());
				String value = DataSetUtil
						.executeScalar(resolver.getConnection(), sql,
								layerBuilder.getParams()).toString().trim();
				if ("".equals(value)) {
					DataTable table = resolver.getHostTable();
					int rowCount = table.getRows().size();
					resolver.selectWithParam(fields.getId(), parentID);
					DataRow parentRow = table.getRows().getItem(rowCount);
					parentRow.setItem(fields.getIsLeaf(), 0);
					resolver.setCommands(EnumSet.of(AdapterCommand.Update));
					return topLayer + "000";
				} else
					return topLayer
							+ addLayer(value.substring(value.length() - 3));
			} catch (Exception ex) {
				return "";
			}
		}
	}

	public static void moveTree(AbstractXmlTableResolver resolver,
			TreeFieldGroup fields, String sourceID, String destID) {
		IParamBuilder idBuilder = getIdParamBuilder(fields, fields.getId(),
				destID);
		String sql = String.format("SELECT %s FROM %s WHERE %s", fields
				.getLayer(), resolver.getTableName(), idBuilder.getSQL());
		String destLayer = DataSetUtil.executeScalar(resolver.getConnection(),
				sql, idBuilder.getParams()).toString();
		DataRow srcRow = resolver.selectRowWithParam(fields.getId(), sourceID);
		if (destID.equals(srcRow.getItem(fields.getParentID().toString())))
			throw new InvalidMoveException();

		String oldLayer = srcRow.getItem(fields.getLayer()).toString();
		if (destLayer.startsWith(oldLayer))
			throw new InvalidMoveException();

		resolver.setCommands(EnumSet.of(AdapterCommand.Update));

		String newLayer = getLayer(resolver, fields, destID);
		srcRow.setItem(fields.getLayer(), newLayer);

		DataTable table = resolver.getHostTable();
		int currentCount = table.getRows().size();

		IParamBuilder layerBuilder = getLayerParamBuilder(fields, oldLayer);
		sql = String.format("WHERE %s", layerBuilder.getSQL());
		resolver.select(sql, layerBuilder.getParams());
		for (int i = currentCount; i < table.getRows().size(); ++i) {
			DataRow row = table.getRows().getItem(i);
			row.setItem(fields.getLayer(), row.getItem(fields.getLayer())
					.toString().replace(oldLayer, newLayer));
		}

		setParentLeaf(resolver, fields, srcRow.getItem(fields.getParentID())
				.toString());
		srcRow.setItem(fields.getParentID(), destID);
	}

	public static void deleteTree(AbstractXmlTableResolver resolver,
			TreeFieldGroup fields, String id) {
		DataRow row = resolver.selectRowWithParam(fields.getId(), id);

		resolver.setCommands(EnumSet.of(AdapterCommand.Update,
				AdapterCommand.Delete));

		DataTable table = resolver.getHostTable();
		int currentCount = table.getRows().size();

		IParamBuilder layerBuilder = getLayerParamBuilder(fields, row.getItem(
				fields.getLayer()).toString());
		String sql = String.format("WHERE %s", layerBuilder.getSQL());
		resolver.select(sql, layerBuilder.getParams());
		for (int i = currentCount; i < table.getRows().size(); ++i) {
			DataRow delRow = table.getRows().getItem(i);
			resolver.deleteRow(delRow);
		}

		setParentLeaf(resolver, fields, row.getItem(fields.getParentID())
				.toString());
		resolver.deleteRow(row);
		// row.Delete();
	}

	public static void delete(AbstractXmlTableResolver resolver,
			TreeFieldGroup fields, String id) {
		DataRow row = resolver.selectRowWithParam(fields.getId(), id);

		resolver.setCommands(EnumSet.of(AdapterCommand.Update,
				AdapterCommand.Delete));

		setParentLeaf(resolver, fields, row.getItem(fields.getParentID())
				.toString());
		row.delete();
	}

	public static void mergeTree(AbstractXmlTableResolver resolver,
			TreeFieldGroup fields, String mergeID, String destID,
			String mergeField, String activeField) {
		IParamBuilder idBuilder = getIdParamBuilder(fields, fields.getId(),
				destID);
		String sql = String.format("SELECT %s FROM %s WHERE %s", fields
				.getLayer(), resolver.getTableName(), idBuilder.getSQL());
		String destLayer = DataSetUtil.executeScalar(resolver.getConnection(),
				sql, idBuilder.getParams()).toString();
		DataRow srcRow = resolver.selectRowWithParam(fields.getId(), mergeID);
		DataTable table = resolver.getHostTable();

		String oldLayer = srcRow.getItem(fields.getLayer()).toString();
		int oldLayerLen = oldLayer.length() + 3;
		if (destLayer.startsWith(oldLayer))
			throw new InvalidMoveException();

		String newLayer = getLayer(resolver, fields, destID);
		int newLayerLen = newLayer.length() - 3;
		if (table.getRows().size() == 1)
			resolver.selectWithParam(fields.getId(), destID);
		DataRow mergeRow = table.getRows().getItem(1);

		mergeRow.setItem(fields.getIsLeaf(), 0);
		String temp = mergeRow.getItem(mergeField).toString();
		if ("".equals(temp))
			mergeRow.setItem(mergeField, String.format("'%s'", srcRow
					.getItem(fields.getId())));
		else
			mergeRow.setItem(mergeField, String.format("%s, '%s'", srcRow
					.getItem(fields.getId()), mergeRow.getItem(mergeField)));

		int currentCount = table.getRows().size();

		IParamBuilder layerBuilder = getLayerParamBuilder(fields, oldLayer);
		sql = String.format("WHERE %s ORDER BY %s", layerBuilder.getSQL(),
				fields.getLayer());
		resolver.select(sql, layerBuilder.getParams());
		String oldLayerValue = "";
		for (int i = currentCount; i < table.getRows().size(); ++i) {
			DataRow row = table.getRows().getItem(i);
			String nowLayer = row.getItem(fields.getLayer()).toString();
			if (nowLayer.length() == oldLayerLen) {
				if ("".equals(oldLayerValue))
					oldLayerValue = newLayer;
				else
					oldLayerValue = oldLayerValue.substring(0, newLayerLen)
							+ addLayer(oldLayerValue.substring(newLayerLen));
				row.setItem(fields.getParentID(), mergeRow.getItem(fields
						.getId()));
				row.setItem(fields.getLayer(), oldLayerValue);
			} else {
				nowLayer = nowLayer.substring(newLayerLen + 3);
				row.setItem(fields.getLayer(), oldLayerValue + nowLayer);
			}
		}
		setParentLeaf(resolver, fields, srcRow.getItem(fields.getParentID())
				.toString());

		srcRow.setItem(activeField, 0);
		srcRow.setItem(fields.getParentID(), -2);
		resolver.setCommands(EnumSet.of(AdapterCommand.Update,
				AdapterCommand.Delete));
	}
}

class InvalidMoveException extends ErrorPageException {
	private static final long serialVersionUID = 1L;

	public InvalidMoveException() {
		// ErrorBody = PageTitle =
		// TKResUtil.GetResourceString("InvalidMove_Body");
		// ErrorTitle = PageTitle =
		// TKResUtil.GetResourceString("InvalidMove_Title");
	}
}
