class RemoveCollectionFromAlbum < ActiveRecord::Migration
  def up
    remove_column :albums, :collection
      end

  def down
    add_column :albums, :collection, :boolean
  end
end
