class RemovePhotoIdFromAssortment < ActiveRecord::Migration
  def up
    remove_column :assortments, :photo_id
      end

  def down
    add_column :assortments, :photo_id, :integer
  end
end
