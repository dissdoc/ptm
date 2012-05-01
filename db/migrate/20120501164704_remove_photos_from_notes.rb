class RemovePhotosFromNotes < ActiveRecord::Migration
  def up
    remove_column :notes, :photo_id
      end

  def down
    add_column :notes, :photo_id, :integer
  end
end
